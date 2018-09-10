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

    @After
    public void destroyRepositoryInstance() {
        manager.destroyInstance();
    }

    private static PingingTask createPingingTask(String description, String address, int timeOutMs) {
        PingingTask task = new PingingTask(description);
        task.settings.setPingAddress(address);
        task.settings.setPingTimeoutMs(timeOutMs);
        return task;
    }

    @Test
    public void addTasks_AddsOneTask() {
        manager.addTask(pingingTask1);
        MonitoringTask returnedTask = manager.getTask(pingingTask1.getTaskId());
        assertNotNull(returnedTask);
        assertThat(returnedTask.getTaskId(), is(pingingTask1.getTaskId()));
    }

    @Test
    public void addTasks_MultipleTasks() {

        addMultipleTasks();
        List<MonitoringTask> tasks = manager.getTasks();

        assertTrue(tasks.contains(pingingTask1));
        assertTrue(tasks.contains(pingingTasks.get(0)));
        assertTrue(tasks.contains(pingingTasks.get(1)));
        assertTrue(tasks.contains(pingingTasks.get(2)));
    }

    private void addMultipleTasks() {
        manager.addTask(pingingTask1);
        manager.addTask(pingingTasks.get(0));
        manager.addTask(pingingTasks.get(1));
        manager.addTask(pingingTasks.get(2));
    }

    @Test
    public void addTasks_NullTask() {
        manager.addTask(null);
        assertThat(manager.getTasks().size(), is (0));
    }

    @Test
    public void getTasks_NoTasksReturnsEmptyList() {
        assertThat(manager.getTasks().size(), is (0));
    }

    @Test
    public void deleteTask_OneTask() {
        addMultipleTasks();
        assertTrue(manager.getTasks().contains(pingingTask1));

        manager.deleteTask(pingingTask1);

        assertFalse(manager.getTasks().contains(pingingTask1));
    }

    @Test
    public void deleteAllTasks() {
        addMultipleTasks();
        manager.deleteAllTasks();
        assertThat(manager.getTasks().size(), is (0));
    }

    @Test
    public void updateOneTask() {
        addMultipleTasks();

        pingingTask1.setDescription("New description for TEST ping task 3");
        pingingTask1.activate();
        pingingTask1.settings.setPingTimeoutMs(1500);
        pingingTask1.settings.setPingAddress("192.168.0.0");

        manager.addTask(pingingTask1);


    }
}