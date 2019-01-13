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

    /**
     * A persistent storage for tasks
     */
    private TasksDataSource dataSource;

    /**
     * In-memory cache of the tasks stored in the data source (package access for testing)
     */
    ConcurrentHashMap<String, MonitoringTask> tasks = new ConcurrentHashMap<>();

    /**
     * The executor to run the manager itself on
     */
    private ExecutorService managerThreadExecutor;

    /**
     * The executors to run I/O and UI operations on
     */
    private AppExecutors executors;

    /**
     * Ensures that tasks are loaded from the data source (loadTasksFromDatasource called) only once - when
     * the app is started
     */
    private volatile boolean loadedTasksFromDatasource = false;

    /**
     * The reference to thread that the manager is running on.
     * Lets us cancel that thread if needed.
     */
    private Future managerThread = null;

    /**
     * Objects that will be notified on the changes in the tasks (adding/deleting/new results)
     */
    private List<StateChangedListener> listeners = new ArrayList<>();

    /**
     * A flag to notify the manager's loop to stop executing tasks and exit
     */
    private volatile boolean stopManager = false;

    @Inject
    public TasksManagerImpl(@NotNull TasksDataSource dataSource, AppExecutors appExecutors, @Named("managerThreadExecutor") ExecutorService managerThreadExecutor) {
        this.dataSource = dataSource;
        this.managerThreadExecutor = managerThreadExecutor;
        this.executors = appExecutors;
    }

    /**
     * Starting the thread that will execute tasks in a loop.
     * The typical use is:
     * TasksManagerStarter starter = dependencyInjection.getTasksManagerImplementation();
     * starter.startManager();
     * TasksManager manager = starter.getManager();
     * manager.getCachedTasks();
     * manager.addTask(...);
     */
    @Override
    public synchronized void startManager() {
        if (managerThread == null)
            managerThread = managerThreadExecutor.submit(this);
    }

    /**
     * Getting the TasksManager interface from the TasksManagerStarter interface
     * @return TasksManager interface to run operations on tasks
     */
    @Override
    public TasksManager getManager() {
        return this;
    }

    /**
     * A loop that executes tasks.
     * Iterates through the list of tasks, and executes those whose next execution
     * time has come. Notifies listeners if a task has a new result to show to the user.
     * Before the loop is started, loads tasks from the data source.
     * After the loop is existed, saves tasks to the data store.
     */
    @WorkerThread
    @Override
    public void run() {
        boolean someTasksWereRun;
        boolean interrupted = false;

        Timber.i("Tasks manager started");

        loadTasksFromDatasource();

        while (!Thread.interrupted() && !interrupted && !stopManager) {
            someTasksWereRun = false;
            try {

                for (MonitoringTask task :
                        tasks.values()) {
                    if (shouldRunTask(task)) {
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

    /**
     * Determines if the time to execute the task has come based on following:
     * 1. The task is not being executed at the moment
     * 2. The task is not deactivated
     * 3. The task's state and settings signal that it is time to execute it (the pause time elapsed)
     * @param task The task to run the check on
     * @return if the time to execute the task has come
     */
    private boolean shouldRunTask(MonitoringTask task) {
        return !task.isWorking() && task.getMode() != MonitoringTask.MODE_DEACTIVATED && task.isTimeToExecute();
    }

    /**
     * Interrupts the worker thread - run()
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

    /**
     * Loads tasks from the data source (synchronously).
     * Call this method when the app starts.
     * Only the first call is effective, subsequent calls do nothing.
     */
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

    /**
     * Returns all existing (in the memory) tasks, without checking the data source.
     */
    @UiThread
    public List<MonitoringTask> getCachedTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Returns all existing tasks, retrieves tasks from the data source on the first call.
     */
    @UiThread
    @Override
    public void getTasks(LoadTasksCallback callback) {
        executors.diskIO().execute(() -> {
            loadTasksFromDatasource();
            executors.mainThread().execute(() -> callback.onTasksLoaded(new ArrayList<>(tasks.values())));
        });
    }

    /*@UiThread
    @Nullable
    @Override
    public MonitoringTask getTask(String taskId) {
        return tasks.get(taskId);
    }
*/

    /**
     * Adds new task or updates an existing one (asynchronously).
     */
    @UiThread
    @Override
    public void addTask(@NonNull MonitoringTask task) {
        if (task == null || tasks.containsKey(task.getTaskId())) return;

        tasks.put(task.getTaskId(), task);
        dataSource.updateOrAddTask(task);
        notifyListenersFromUI(task, StateChangedListener.ADDED);
    }

    /**
     * Deletes all tasks from the memory and the data source (asynchronously).
     */
    @UiThread
    @Override
    public void deleteAllTasks() {
        tasks.clear();
        dataSource.deleteAllTasks();
    }

    /**
     * Deletes a task from the memory and the data source (asynchronously).
     */
    @UiThread
    @Override
    public void deleteTask(@NonNull MonitoringTask task) {
        if (task == null) return;
        tasks.remove(task.getTaskId());
        dataSource.deleteTask(task);
        notifyListenersFromUI(task, StateChangedListener.DELETED);
    }


    /**
     * Notifies listeners about a change (status update) in the task list (a task added/deleted/got a new result).
     * This method just executes notifyListenersFromUI on the UI thread.
     * @param changedTask The task that the change is related to. Null if a few tasks are affected.
     * @param whatChanged The reason for the status updated - what changed in the task.
     */
    @WorkerThread
    private void notifyListeners(@Nullable MonitoringTask changedTask, @StateChangedListener.WhatChanged int whatChanged) {
        executors.mainThread().execute(() -> notifyListenersFromUI(changedTask, whatChanged));
    }

    /**
     * Notifies listeners about a change (status update) in the task list (a task added/deleted/got a new result).
     * Should be called from UI thread, since the method performs the notification call to the UI thread.
     * @param changedTask The task that the change is related to. Null if a few tasks are affected.
     * @param whatChanged The reason for the status updated - what changed in the task.
     */
    @UiThread
    private void notifyListenersFromUI(@Nullable MonitoringTask changedTask, @StateChangedListener.WhatChanged int whatChanged) {
        int taskPositionInTheList = changedTask != null ? getCachedTasks().indexOf(changedTask) : -1;
        for (StateChangedListener listener :
                listeners) {
            listener.onTaskStateChanged(changedTask, taskPositionInTheList, whatChanged);
        }
    }

    /**
     * Adds a listener that will be notified about changes in the task list (a task added/deleted/got a new result).
     * @param callback The {@link StateChangedListener} implementation to listen to the changes.
     */
    @UiThread
    @Override
    public void addTaskStateChangedListener(@NonNull StateChangedListener callback) {
        Timber.i("Adding listener: %s", callback.toString());
        if (listeners.size() > 0) // Generally, there should be only one listener - the tasks activity
            Timber.wtf("Adding second listener. Possible memory leak!");
        listeners.add(callback);
    }

    /**
     * Removes the previously added callback.
     */
    @Override
    public void removeTaskStateChangedListener(@NonNull StateChangedListener callback) {
        Timber.i("Removing listener: %s", callback.toString());
        listeners.remove(callback);
    }
}
