package com.evartem.remsimon.data;

import com.evartem.remsimon.FakeDataSource;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.types.pinging.PingingTask;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class TasksManagerTest {

    TasksManager manager;

    PingingTask pingingTask1 = createPingingTask("TEST ping task 1", "127.0.0.1", 1000);

    List<PingingTask> pingingTasks = Arrays.asList(createPingingTask("TEST ping task 2", "127.0.0.2", 2000),
            createPingingTask("TEST ping task 3", "127.0.0.3", 3000),
            createPingingTask("TEST ping task 4", "127.0.0.4", 4000));

    @Before
    public void setUp() {
        manager = TasksManager.getInstance(new FakeDataSource(), Executors.newFixedThreadPool(1));
        manager.startManager();
    }

    private static PingingTask createPingingTask(String description, String address, int timeOutMs) {
        PingingTask task = new PingingTask(description);
        task.settings.setPingAddress(address);
        task.settings.setPingTimeoutMs(timeOutMs);
        return task;
    }

    @Test
    public void AddTasks_AddsOneTask() {
        manager.addTask(pingingTask1);
        MonitoringTask returnedTask = manager.getTask(pingingTask1.getTaskId());
        assertNotNull(returnedTask);
        assertThat(returnedTask.getTaskId(), is(pingingTask1.getTaskId()));
    }

    @Test
    public void AddTasks_MultipleTasks() {
        manager.addTask(pingingTask1);
        manager.addTask(pingingTasks.get(0));
        manager.addTask(pingingTasks.get(1));
        manager.addTask(pingingTasks.get(2));

        List<MonitoringTask> tasks = manager.getTasks();

        assertTrue(tasks.contains(pingingTask1));
        assertTrue(tasks.contains(pingingTasks.get(0)));
        assertTrue(tasks.contains(pingingTasks.get(1)));
        assertTrue(tasks.contains(pingingTasks.get(2)));
    }


    @After
    public void tearDown() {
        //assertThat(manager.finish(), is(true));
    }
}