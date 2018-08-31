package com.evartem.remsimon.data;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

public interface TasksDataSource {

    /**
     * Returns (in the callback) the list of all existing tasks (either from memory cache or from the datasource)
     * @param callback The method that will receive the data
     */
    void getTasks(@NonNull LoadTasksListener callback);

    interface LoadTasksListener {
        @UiThread
        void onTasksLoaded(@NonNull List<MonitoringTask> tasks);
    }

    void setTaskStateChangedListener(@NonNull StateChangedListener callback);

    interface StateChangedListener {

        @RestrictTo(LIBRARY_GROUP)
        @IntDef({STATE_CHANGED, DELETED, ADDED})
        @IntRange(from = 1)
        @Retention(RetentionPolicy.SOURCE)
        public @interface WhatChanged {}

        public static final int STATE_CHANGED = 1;
        public static final int DELETED = 2;
        public static final int ADDED = 3;

        @UiThread
        void onTaskStateChanged(@NonNull MonitoringTask changedTask, @WhatChanged int whatChanged);
    }

    void saveTask(@NonNull MonitoringTask task);

    void saveTasks(@NonNull List<MonitoringTask> tasks);

    void deleteAllTasks();

    void deleteTask(@NonNull MonitoringTask task);
}
