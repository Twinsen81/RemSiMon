package com.evartem.remsimon.data.source.local;

import android.support.annotation.NonNull;

import com.evartem.remsimon.data.MonitoringTask;
import com.evartem.remsimon.data.TasksDataSource;

import java.util.List;

public class TasksLocalDataSource implements TasksDataSource {

    private static volatile TasksLocalDataSource INSTANCE = null;

    public static TasksLocalDataSource getInstance() {
        if (INSTANCE == null) {
            synchronized (TasksLocalDataSource.class) {
                if (INSTANCE == null)
                    INSTANCE = new TasksLocalDataSource();
            }
        }
        return INSTANCE;
    }

    //private TasksLocalDataSource()


    @Override
    public void getTasks(@NonNull LoadTasksListener callback) {

    }

    @Override
    public void setTaskStateChangedListener(@NonNull StateChangedListener callback) {

    }

    @Override
    public void saveTask(@NonNull MonitoringTask task) {

    }

    @Override
    public void saveTasks(@NonNull List<MonitoringTask> tasks) {

    }

    @Override
    public void deleteAllTasks() {

    }

    @Override
    public void deleteTask(@NonNull MonitoringTask task) {

    }
}
