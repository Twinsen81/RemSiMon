package com.evartem.remsimon;

import android.support.annotation.NonNull;

import com.evartem.remsimon.data.source.TasksDataSource;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.types.pinging.PingingTask;
import com.evartem.remsimon.util.AppExecutors;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
            callback.onTasksLoaded(new ArrayList<>(tasksDb));
        });
    }

    @Override
    public List<MonitoringTask> getTasksSync() {
        return new ArrayList<>(tasksDb);
    }

    @Override
    public void updateOrAddTask(@NonNull MonitoringTask task) {
        updateOrAddTasks(Collections.singletonList(task));
    }

    @Override
    public void updateOrAddTasks(@NonNull List<MonitoringTask> tasks) {
        System.out.println("FAKE DATA SOURCE: ADD");
        for (MonitoringTask newTask :
                tasks) {
            int index2Update = tasksDb.indexOf(newTask);
            if (index2Update == -1)
                tasksDb.add(newTask);
            else
                tasksDb.get(index2Update).copyPropertiesFrom(newTask);
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
