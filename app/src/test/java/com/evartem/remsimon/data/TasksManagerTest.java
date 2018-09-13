package com.evartem.remsimon.data;

import com.evartem.remsimon.data.source.TasksDataSource;
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
import static org.mockito.Answers.RETURNS_DEFAULTS;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.stubbing.answers.AnswersWithDelay;

public class TasksManagerTest {

    @Mock
    private TasksDataSource mockDataSource;

    private TasksManager manager;

    private PingingTask pingingTask1 = createPingingTask("TEST ping task 1", "127.0.0.1", 1000);

    private List<PingingTask> pingingTasks = Arrays.asList(createPingingTask("TEST ping task 2", "127.0.0.2", 2000),
            createPingingTask("TEST ping task 3", "127.0.0.3", 3000),
            createPingingTask("TEST ping task 4", "127.0.0.4", 4000));


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        manager = TasksManager.getInstance(mockDataSource, Executors.newFixedThreadPool(1));
        manager.startManager();
    }

    @After
    public void destroyRepositoryInstance() {
        // Make sure each test method gets its own manager instance
        TasksManager.destroyInstance();
    }


    @Test
    public void addTasks_AddsOneTask() {
        // Given a task - pingingTask1
        // When a task is added to the manager
        manager.addTask(pingingTask1);

        // Then the task is added to to the data source
        verify(mockDataSource).updateOrAddTask(pingingTask1);
        // and to the manager's cache
        assertThat(manager.tasks.size(), is(1));
    }

    @Test
    public void addTasks_MultipleTasks() {
        // Given a few tasks
        // When they are added to the manager
        int numberOfTasks = addMultipleTasks();

        // Then they are added to the cache
        assertThat(manager.tasks.size(), is(numberOfTasks));
        // and to the data source
        verify(mockDataSource, times(numberOfTasks)).updateOrAddTask(any());
    }

    @Test
    public void addTasks_NullTask() {
        // When adding null instead of a task
        manager.addTask(null);

        // Then the corresponding data source method is not called
        verify(mockDataSource, never()).updateOrAddTask(any());
        // and nothing is added to the cache
        assertThat(manager.getTasks().size(), is(0));
    }

    @Test
    public void addTasks_SameTaskTwice() {
        // Given one task in the manager
        manager.addTask(pingingTask1);

        // When adding the same task again
        manager.addTask(pingingTask1);

        // Then the corresponding data source method is called only once
        verify(mockDataSource).updateOrAddTask(any());
        // and the cache size is still 1
        assertThat(manager.getTasks().size(), is(1));
    }

    @Test
    public void getTasks_NoTasksReturnsEmptyList()
    {
        // Given no tasks in the manager
        // When requesting the cache
        List<MonitoringTask> tasks = manager.getTasks();

        // Then an empty list is returned
        assertNotNull(tasks);
        assertThat(tasks.size(), is(0));
    }

    @Test
    public void deleteTask_OneTask() {
        // Given a few tasks in the manager
        addMultipleTasks();

        // When one of the tasks is deleted
        manager.deleteTask(pingingTask1);

        // Then make sure that task is gone from the cache
        assertFalse(manager.getTasks().contains(pingingTask1));
        // and the corresponding method is called on the data source
        verify(mockDataSource).deleteTask(pingingTask1);
    }

    @Test
    public void deleteAllTasks() {
        // Given a few tasks in the manager
        addMultipleTasks();

        // When all the tasks are deleted
        manager.deleteAllTasks();

        // Then there's nothing in the cache
        assertThat(manager.getTasks().size(), is(0));
        // and the corresponding method is called on the data source
        verify(mockDataSource).deleteAllTasks();
    }

    @Test
    public void taskIsRunByManager() {
        // Given a task
        PingingTask task = Mockito.mock(PingingTask.class);
        when(task.getTaskId()).thenReturn("TestTaskID");
        when(task.isTimeToExecute()).thenReturn(true).thenReturn(false);

        // When the task is added to the manager
        manager.addTask(task);

        // Then it is executed at least once
        verify(task, timeout(3000).times(1)).doTheWork();
    }

    @Test
    public void gracefullyFinishManager() throws InterruptedException {

        // Given a task
        PingingTask task = Mockito.mock(PingingTask.class);
        when(task.getTaskId()).thenReturn("TestTaskID");
        when(task.isTimeToExecute()).thenReturn(true).thenReturn(false);
        doAnswer(new AnswersWithDelay(100, RETURNS_DEFAULTS)).when(task).doTheWork();

        // When the task is added to the manager
        // and the manager is shut down
        manager.addTask(task);
        verify(mockDataSource, times(1)).updateOrAddTask(task);
        Thread.sleep(100);
        Boolean finishedGracefully = manager.finish();

        // Then the manager's worker thread finishes properly
        assertTrue(finishedGracefully);
        // and the data is saved to the data source
        verify(mockDataSource, times(1)).updateOrAddTasks(any());

    }

    // Convenience methods

    private static PingingTask createPingingTask(String description, String address, int timeOutMs) {
        PingingTask task = new PingingTask(description);
        task.settings.setPingAddress(address);
        task.settings.setPingTimeoutMs(timeOutMs);
        return task;
    }

    private int addMultipleTasks() {
        manager.addTask(pingingTask1);
        manager.addTask(pingingTasks.get(0));
        manager.addTask(pingingTasks.get(1));
        manager.addTask(pingingTasks.get(2));
        return 4;
    }
}