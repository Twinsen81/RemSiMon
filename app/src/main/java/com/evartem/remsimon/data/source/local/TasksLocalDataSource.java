package com.evartem.remsimon.data.source.local;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.source.TasksDataSource;

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

    private TasksLocalDataSource() {}


    @Override
    public void getTasks(@NonNull LoadTasksListener callback) {

    }

    @Override
    @WorkerThread
    public List<MonitoringTask> getTasksSync() {
        return null;
    }


    @Override
    @UiThread
    public void saveOrAddTask(@NonNull MonitoringTask task) {

    }

    /**
     * If called from UI thread -> executes asynchronously
     * If called from Worker thread -> synchronously
     * @param tasks
     */
    @Override
    public void saveOrAddTasks(@NonNull List<MonitoringTask> tasks) {

    }

    @Override
    @UiThread
    public void deleteAllTasks() {

    }

    @Override
    @UiThread
    public void deleteTask(@NonNull MonitoringTask task) {

    }
}
