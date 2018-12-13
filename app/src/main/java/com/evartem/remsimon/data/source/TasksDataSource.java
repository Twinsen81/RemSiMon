package com.evartem.remsimon.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.evartem.remsimon.data.types.base.MonitoringTask;

import java.util.List;

/**
 * Common interface for all data sources of tasks
 */
public interface TasksDataSource {

    /**
     * Returns (in the callback) the list of all existing tasks.
     * Non-blocking call. The callback method is called on the UI thread.
     * @param callback The method that will receive the data
     */
    void getTasks(@NonNull LoadTasksListener callback);

    interface LoadTasksListener {
        @UiThread
        void onTasksLoaded(@NonNull List<MonitoringTask> tasks);
    }

    /**
     * Returns the list of all existing tasks.
     * A blocking call - must be called on a worker thread.
     * @return
     */
    @WorkerThread
    List<MonitoringTask> getTasksSync();

    /**
     * Adds a new task or updates the existing one
     * @param task
     */
    @UiThread
    void updateOrAddTask(@NonNull MonitoringTask task);

    /**
     * Adds new tasks or updates them if they already exist
     * If called from UI thread -> executes asynchronously
     * If called from Worker thread -> synchronously
     * @param tasks
     */
    void updateOrAddTasks(@NonNull List<MonitoringTask> tasks);

    /**
     * Delete all tasks from this data source
     */
    @UiThread
    void deleteAllTasks();

    /**
     * Delete the provided task form the data source
     * @param task
     */
    @UiThread
    void deleteTask(@NonNull MonitoringTask task);
}
