package com.evartem.remsimon.data.source;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.evartem.remsimon.data.types.base.MonitoringTask;

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

    @WorkerThread
    List<MonitoringTask> getTasksSync();


    @UiThread
    void updateOrAddTask(@NonNull MonitoringTask task);

    /**
     * If called from UI thread -> executes asynchronously
     * If called from Worker thread -> synchronously
     * @param tasks
     */
    void updateOrAddTasks(@NonNull List<MonitoringTask> tasks);

    @UiThread
    void deleteAllTasks();

    @UiThread
    void deleteTask(@NonNull MonitoringTask task);
}
