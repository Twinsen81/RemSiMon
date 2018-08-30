package com.evartem.remsimon.data;

import android.support.annotation.NonNull;

import java.util.List;

public interface TasksDataSource {

    void getTasks(@NonNull LoadTasksCallback callback);

    interface LoadTasksCallback {
        void onTasksLoaded(@NonNull List<MonitoringTask> tasks);
    }

    void setTaskStateChangedListener(@NonNull StateChangedCallback callback);

    interface StateChangedCallback {
        void onTaskStateChanged(@NonNull MonitoringTask changedTask);
    }

    void saveTask(@NonNull MonitoringTask task);

    void saveTasks(@NonNull List<MonitoringTask> tasks);

    void deleteAllTasks();

    void deleteTask(@NonNull MonitoringTask task);
}
