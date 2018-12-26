package com.evartem.remsimon.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.evartem.remsimon.data.source.TasksDataSource;
import com.evartem.remsimon.data.types.TasksManagerStarter;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.util.AppExecutors;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import timber.log.Timber;

/**
 * A repository for the tasks and a manager responsible for executing the tasks.
 * Should be kept alive during the app's lifecycle, even when activities are destroyed and
 * the app is in the background. Hence, must be run in a service or somehow else (Scheduler?)
 */
public class TasksManagerImpl implements TasksManager, TasksManagerStarter, Runnable {

    //private static TasksManagerImpl INSTANCE = null;
    private TasksDataSource dataSource;
    ConcurrentHashMap<String, MonitoringTask> tasks = new ConcurrentHashMap<>(); // In-memory cache of the tasks stored in the data source (package access for testing)

    private ExecutorService managerThreadExecutor;
    private AppExecutors executors;

    private volatile boolean loadedTasksFromDatasource = false;
    //private volatile boolean successfullyFinishedWorking = false;
    private Future managerThread = null;

    private List<StateChangedListener> listeners = new ArrayList<>();

    private volatile boolean stopManager = false;

    @Inject
    public TasksManagerImpl(@NotNull TasksDataSource dataSource, AppExecutors appExecutors, @Named("managerThreadExecutor") ExecutorService managerThreadExecutor) {
        this.dataSource = dataSource;
        this.managerThreadExecutor = managerThreadExecutor;
        this.executors = appExecutors;
    }

    /**
     * Used to force new instance creation (for tests)
     */
/*    public static void destroyInstance() {
        synchronized (TasksManagerImpl.class) {
            INSTANCE = null;
        }
    }*/

    /**
     * Strating the thread that will execute tasks
     */
    @Override
    public synchronized void startManager() {
        if (managerThread == null)
            managerThread = managerThreadExecutor.submit(this);
    }

    @Override
    public TasksManager getManager() {
        return this;
    }

    @WorkerThread
    @Override
    public void run() {
        boolean someTasksWereRun;
        boolean interrupted = false;

        Timber.i("Tasks manager started");

        // Load tasks that were previously saved to the datasource
        loadTasksFromDatasource();

        while (!Thread.interrupted() && !interrupted && !stopManager) {
            someTasksWereRun = false;
            try {

                for (MonitoringTask task :
                        tasks.values()) {
                    if (shouldRunTask(task)) {
                        //Timber.i("Executing task: %s", task.getDescription());
                        task.doTheWork();
                        someTasksWereRun = true;
                        if (task.gotNewResult()) {
                            notifyListeners(task, StateChangedListener.STATE_CHANGED);
                        }
                    }

                }

                if (!someTasksWereRun) TimeUnit.MILLISECONDS.sleep(20);

            } catch (InterruptedException e) {
                interrupted = true;
            }
        }

        forceSaveAll2Datasource();
        Timber.i("Tasks manager finished");
    }

    private boolean shouldRunTask(MonitoringTask task) {
        return !task.isWorking() && task.getMode() != MonitoringTask.MODE_DEACTIVATED && task.isTimeToExecute();
    }

    /**
     * Interrupts the worker thread (run)
     *
     * @throws InterruptedException
     */
    @Override
    public void finish() throws InterruptedException {
        listeners.clear();
        stopManager = true;
        if (managerThread != null) {
            managerThread.cancel(true);
            managerThreadExecutor.shutdownNow();
            managerThreadExecutor.awaitTermination(2000, TimeUnit.MILLISECONDS);
        }
    }


    /**
     * Saves current state of all tasks to the datasource
     */
    public void forceSaveAll2Datasource() {
        if (tasks.size() > 0)
            dataSource.updateOrAddTasks(new ArrayList<>(tasks.values()));
    }

    @WorkerThread
    private synchronized void loadTasksFromDatasource() {
        if (loadedTasksFromDatasource) return;

        List<MonitoringTask> dbTasks = dataSource.getTasksSync();
        for (MonitoringTask dbTask :
                dbTasks) {
            tasks.put(dbTask.getTaskId(), dbTask);
        }

        loadedTasksFromDatasource = true;
    }


    @UiThread
    @Override
    public List<MonitoringTask> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @UiThread
    @Override
    public void getTasks(LoadTasksCallback callback) {
        executors.diskIO().execute(() -> {
            loadTasksFromDatasource();
            executors.mainThread().execute(() -> callback.onTasksLoaded(new ArrayList<>(tasks.values())));
        });
    }

    @UiThread
    @Nullable
    @Override
    public MonitoringTask getTask(String taskId) {
        return tasks.get(taskId);
    }

    @UiThread
    @Override
    public void addTask(@NonNull MonitoringTask task) {
        if (task == null || tasks.containsKey(task.getTaskId())) return;

        tasks.put(task.getTaskId(), task);
        dataSource.updateOrAddTask(task);
        notifyListenersFromUI(task, StateChangedListener.ADDED);
    }

    @UiThread
    @Override
    public void deleteAllTasks() {
        tasks.clear();
        dataSource.deleteAllTasks();
    }

    @UiThread
    @Override
    public void deleteTask(@NonNull MonitoringTask task) {
        if (task == null) return;
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
        int taskPositionInTheList = getTasks().indexOf(changedTask);
        for (StateChangedListener listener :
                listeners) {
            listener.onTaskStateChanged(changedTask, taskPositionInTheList, whatChanged);
        }
    }

    @Override
    public void addTaskStateChangedListener(@NonNull StateChangedListener callback) {
        Timber.i("Adding listener: %s", callback.toString());
        if (listeners.size() > 0) // Generally, there should be only one listener - the tasks activity
        {
            Timber.wtf("Adding second listener. Possible memory leak!");
        }
        listeners.add(callback);
    }

    @Override
    public void removeTaskStateChangedListener(@NonNull StateChangedListener callback) {
        Timber.i("Removing listener: %s", callback.toString());
        listeners.remove(callback);
    }
}
