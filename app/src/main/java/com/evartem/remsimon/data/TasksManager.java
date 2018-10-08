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

public interface TasksManager {


    void forceSaveAll2Datasource();

    List<MonitoringTask> getTasks();

    void getTasks(LoadTasksCallback callback);

    MonitoringTask getTask(String taskId);

    void addTask(@NonNull MonitoringTask task);

    void deleteAllTasks();

    void deleteTask(@NonNull MonitoringTask task);

    void addTaskStateChangedListener(@NonNull StateChangedListener callback);

    void removeTaskStateChangedListener(@NonNull StateChangedListener callback);

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

    interface LoadTasksCallback {

        void onTasksLoaded(List<MonitoringTask> tasks);

    }
}
