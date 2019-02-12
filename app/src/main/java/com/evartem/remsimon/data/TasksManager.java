package com.evartem.remsimon.data;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.UiThread;

import com.evartem.remsimon.data.types.base.MonitoringTask;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/**
 * Defines an API to add/change/remove tasks and get notified when the task state changes.
 * The implementation will handle storing tasks and executing them.
 */
public interface TasksManager {

    /**
     * Forces flushing all tasks (i.e. their settings and saved last result) form memory
     * to persistent storage (e.g. a DB).
     */
    void forceSaveAll2Datasource();

    /**
     * Returns the list of existing tasks. This is blocking call.
     * @return The list of objects implementing MonitoringTasks.
     */
    List<MonitoringTask> getCachedTasks();



    /**
     * Returns the list of existing tasks in a callback.
     * The callback will be called on the UI thread.
     * @param callback Will be called with the list of tasks as the parameter
     */
    void getTasks(LoadTasksCallback callback);

    /**
     * Gets the task object given its ID
     */
    MonitoringTask getTaskById(String taskId);

    /**
     * Adds a new task or updates an existing one (memory and datasource)
     */
    void addTask(@NonNull MonitoringTask task);

    /**
     * Deletes all tasks (from memory and the data source)
     */
    void deleteAllTasks();

    /**
     * Delete the given task
     */
    void deleteTask(@NonNull MonitoringTask task);

    /**
     * Add a callback to listen to changes in tasks, like adding/removing a task, or getting new results
     */
    void addTaskStateChangedListener(@NonNull StateChangedListener callback);

    /**
     * Removes the callback
     */
    void removeTaskStateChangedListener(@NonNull StateChangedListener callback);

    /**
     * Stops the manager's worker thread
     */
    void finish() throws InterruptedException;

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
        void onTaskStateChanged(@Nullable MonitoringTask changedTask, int taskPositionInTheList, @WhatChanged int whatChanged);

    }

    interface LoadTasksCallback {

        void onTasksLoaded(List<MonitoringTask> tasks);

    }
}
