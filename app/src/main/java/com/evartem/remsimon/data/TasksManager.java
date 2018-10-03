package com.evartem.remsimon.data;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.evartem.remsimon.data.source.TasksDataSource;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.util.AppExecutors;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/**
 * A repository for the tasks and a manager responsible for executing the tasks.
 * Should be kept alive during the app's lifecycle, even when activities are destroyed and
 * the app is in the background. Hence, must be run in a service or somehow else (Scgeduler?)
 */
public class TasksManager implements Runnable {

    private static TasksManager INSTANCE = null;
    private TasksDataSource dataSource;
    ConcurrentHashMap<String, MonitoringTask> tasks = new ConcurrentHashMap<>(); // In-memory cache of the tasks stored in the data source (package access for testing)

    private ExecutorService managerThreadExecutor;
    private AppExecutors executors;

    private volatile boolean successfullyFinishedWorking = false;
    private Future managerThread = null;

    private List<StateChangedListener> listeners = new ArrayList<>();

    public static TasksManager getInstance(@NotNull TasksDataSource dataSource, AppExecutors appExecutors, ExecutorService managerThreadExecutor) {
        if (INSTANCE == null) {
            synchronized (TasksManager.class) {
                if (INSTANCE == null)
                    INSTANCE = new TasksManager(dataSource, appExecutors, managerThreadExecutor);
            }
        }
        return INSTANCE;
    }

    private TasksManager(@NotNull TasksDataSource dataSource, AppExecutors appExecutors, ExecutorService managerThreadExecutor) {
        this.dataSource = dataSource;
        this.managerThreadExecutor = managerThreadExecutor;
        this.executors = appExecutors;
    }

    /**
     * Used to force new instance creation (for tests)
     */
    public static void destroyInstance() {
        synchronized (TasksManager.class) {
            INSTANCE = null;
        }
    }

    public void startManager() {
        managerThread = managerThreadExecutor.submit(this);
    }

    @Override
    public void run() {
        boolean someTasksWereRun;
        boolean interrupted = false;

        // Load tasks that were previously saved to the datasource
        loadTasksFromDatasource();

        while (!Thread.interrupted() && !interrupted) {
            someTasksWereRun = false;
            try {

                for (MonitoringTask task :
                        tasks.values()) {
                    if (shouldRunTask(task)) {
                        task.doTheWork();
                        someTasksWereRun = true;
                        if (task.gotNewResult())
                            notifyListeners(task, StateChangedListener.STATE_CHANGED);
                    }

                }

                if (!someTasksWereRun) TimeUnit.MILLISECONDS.sleep(20);

            } catch (InterruptedException e) {
                interrupted = true;
            }
        }

        forceSaveAll2Datasource();
        successfullyFinishedWorking = true;
    }

    private boolean shouldRunTask(MonitoringTask task) {
        return !task.isWorking() && task.getMode() != MonitoringTask.MODE_DEACTIVATED && task.isTimeToExecute();
    }

    /**
     * Interrupts the worker thread (run)
     *
     * @return True if the worker thread finished correctly
     * @throws InterruptedException
     */
    public boolean finish() throws InterruptedException {
        listeners.clear();
        if (managerThread != null) {
            managerThread.cancel(true);
            System.out.println("after cancel");
            managerThreadExecutor.shutdownNow();
            managerThreadExecutor.awaitTermination(2000, TimeUnit.MILLISECONDS);
            return successfullyFinishedWorking;
        } else
            return true;
    }


    /**
     * Saves current state of all tasks in the datasource
     */
    public void forceSaveAll2Datasource() {
        if (tasks.size() > 0)
            dataSource.updateOrAddTasks(new ArrayList<MonitoringTask>(tasks.values()));
    }

    /**
     * Updates current cached list of tasks with tasks from the data source
     *
     * @param overwriteExistingTasks if the task already exists in the cache, should its properties
     *                               be overwritten by the task in the data source?
     */
    /*@UiThread
    public void getTasksFromDatasource(boolean overwriteExistingTasks) {
        executors.diskIO().execute(() -> {
            List<MonitoringTask> dbTasks = dataSource.getTasksSync();
            for (MonitoringTask dbTask :
                    dbTasks) {
                MonitoringTask currCachedTask = tasks.get(dbTask.getTaskId());
                if (currCachedTask == null) {
                    tasks.put(dbTask.getTaskId(), dbTask);
                } else if (overwriteExistingTasks) {
                    currCachedTask.copyPropertiesFrom(dbTask);
                }
            }
        });
    }
    */

    @WorkerThread
    private void loadTasksFromDatasource() {
        List<MonitoringTask> dbTasks = dataSource.getTasksSync();
        for (MonitoringTask dbTask :
                dbTasks) {
            tasks.put(dbTask.getTaskId(), dbTask);
        }
        if (dbTasks.size() > 0)
            notifyListeners(null, StateChangedListener.ADDED);
    }


    @UiThread
    public List<MonitoringTask> getTasks() {
        return new ArrayList<MonitoringTask>(tasks.values());
    }

    @UiThread
    @Nullable
    public MonitoringTask getTask(String taskId) {
        return tasks.get(taskId);
    }

    @UiThread
    public void addTask(@NonNull MonitoringTask task) {
        if (task == null || tasks.containsKey(task.getTaskId())) return;

        tasks.put(task.getTaskId(), task);
        dataSource.updateOrAddTask(task);
        notifyListenersFromUI(task, StateChangedListener.ADDED);
    }

    @UiThread
    public void deleteAllTasks() {
        tasks.clear();
        dataSource.deleteAllTasks();
    }

    @UiThread
    public void deleteTask(@NonNull MonitoringTask task) {
        tasks.remove(task.getTaskId());
        dataSource.deleteTask(task);
        notifyListenersFromUI(task, StateChangedListener.DELETED);
    }


    @WorkerThread
    private void notifyListeners(@Nullable MonitoringTask changedTask, @StateChangedListener.WhatChanged int whatChanged) {
        executors.mainThread().execute(() -> notifyListenersFromUI(changedTask, whatChanged));
    }

    @UiThread
    private void notifyListenersFromUI(@Nullable MonitoringTask changedTask, @StateChangedListener.WhatChanged int whatChanged) {
        for (StateChangedListener listener :
                listeners) {
            listener.onTaskStateChanged(changedTask, whatChanged);
        }
    }

    /**
     * Notifies the subscriber about changes in the tasks:
     * ADDED - a new task was added
     * DELETED - a task was deleted
     * STATE_CHANGED - task's work completed
     */
    interface StateChangedListener {

        @RestrictTo(LIBRARY_GROUP)
        @IntDef({STATE_CHANGED, DELETED, ADDED})
        @IntRange(from = 1)
        @Retention(RetentionPolicy.SOURCE)
        public @interface WhatChanged {
        }

        public static final int STATE_CHANGED = 1;
        public static final int DELETED = 2;
        public static final int ADDED = 3;

        @UiThread
        void onTaskStateChanged(@Nullable MonitoringTask changedTask, @WhatChanged int whatChanged);

    }

    void addTaskStateChangedListener(@NonNull StateChangedListener callback) {
        if (listeners.size() > 0) // Generally, there should be only one listener - the tasks activity
            Timber.wtf("Adding second listener. Possible memory leak!");
        listeners.add(callback);
    }

    void removeTaskStateChangedListener(@NonNull StateChangedListener callback) {
        listeners.remove(callback);
    }

}
