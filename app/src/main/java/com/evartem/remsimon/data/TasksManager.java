package com.evartem.remsimon.data;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.UiThread;

import com.evartem.remsimon.data.source.TasksDataSource;
import com.evartem.remsimon.data.types.base.MonitoringTask;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import timber.log.Timber;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/**
 * A repository for the tasks and a manager responsible for executing the tasks.
 * Should be kept alive during the app's lifecycle, even when activities are destroyed and
 * the app is in the background. Hence, the app must use a service.
 */
public class TasksManager implements Runnable {

    private static TasksManager INSTANCE = null;
    private TasksDataSource dataSource;
    private ReentrantLock tasksListLock = new ReentrantLock();
    private ConcurrentHashMap<String, MonitoringTask> tasks = new ConcurrentHashMap<>(); // In-memory cache of the tasks stored in the data source
    private ExecutorService managerThreadExecutor;

    public static TasksManager getInstance(@NotNull TasksDataSource dataSource, ExecutorService managerThreadExecutor) {
        if (INSTANCE == null) {
            synchronized (TasksManager.class) {
                if (INSTANCE == null)
                    INSTANCE = new TasksManager(dataSource, managerThreadExecutor);
            }
        }
        return INSTANCE;
    }


    private TasksManager(@NotNull TasksDataSource dataSource, ExecutorService managerThreadExecutor) {
        dataSource = dataSource;
        this.managerThreadExecutor = managerThreadExecutor;
    }

    public void startManager() {
        managerThreadExecutor.execute(this);
    }

    @Override
    public void run() {
        boolean someTasksWereRun;

        while (!Thread.interrupted()) {
            someTasksWereRun = false;
            try {

                for (MonitoringTask task :
                        tasks.values()) {
                    if (shouldRunTask(task)) {
                        task.doTheWork();
                        someTasksWereRun = true;
                    }

                }

                if (!someTasksWereRun) TimeUnit.MILLISECONDS.sleep(300);

            } catch (InterruptedException e) {
                e.printStackTrace();
                Timber.d(e);
            } finally {
            }
        }

        saveAll2Datasource();
    }

    private boolean shouldRunTask(MonitoringTask task) {
        return !task.isWorking() && task.getMode() != MonitoringTask.MODE_DEACTIVATED && task.isTimeToExecute();
    }

    /**
     * Interrupts all the worker threads
     */
    public void finish() {
        managerThreadExecutor.shutdownNow();
    }


    /**
     * Saves current state of all tasks in the datasource
     */
    private void saveAll2Datasource() {
        if (tasks.size() > 0)
            dataSource.updateOrAddTasks((List<MonitoringTask>) tasks.values());
    }


    @UiThread
    public List<MonitoringTask> getTasks() {
        return (List<MonitoringTask>) tasks.values();
    }

    @UiThread
    public void AddTask(@NonNull MonitoringTask task) {
        if (tasks.containsKey(task.getTaskId())) return;

        tasks.put(task.getTaskId(), task);
        dataSource.updateOrAddTask(task);
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
    }


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
        void onTaskStateChanged(@NonNull MonitoringTask changedTask, @WhatChanged int whatChanged);

    }

    void setTaskStateChangedListener(@NonNull StateChangedListener callback) {

    }

}
