package com.evartem.remsimon;

import android.support.annotation.NonNull;

import com.evartem.remsimon.data.source.TasksDataSource;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.util.AppExecutors;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Fake data source for testing. Holds all items in memory.
 */
public class FakeDataSource implements TasksDataSource{

    private List<MonitoringTask> tasksDb = new ArrayList<>();

    @Override
    public void getTasks(@NonNull LoadTasksListener callback) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(800); // Simulate long running io operations
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            callback.onTasksLoaded(tasksDb);
        });
    }

    @Override
    public List<MonitoringTask> getTasksSync() {
        return tasksDb;
    }

    @Override
    public void updateOrAddTask(@NonNull MonitoringTask task) {
        updateOrAddTasks(Arrays.asList(task));
    }

    /**
     * No need to update tasks since we're working with the same in-memory list
     * @param tasks
     */
    @Override
    public void updateOrAddTasks(@NonNull List<MonitoringTask> tasks) {
        for (MonitoringTask newTask :
                tasksDb) {
            if (!tasksDb.contains(newTask))
                tasksDb.add(newTask);
        }
    }

    @Override
    public void deleteAllTasks() {
        tasksDb.clear();
    }

    @Override
    public void deleteTask(@NonNull MonitoringTask task) {
        tasksDb.removeIf(id -> task.getTaskId().equals(id.getTaskId()));
    }
}
