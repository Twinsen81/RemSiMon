package com.evartem.remsimon.data;

import com.evartem.remsimon.di.AppModule;
import com.evartem.remsimon.data.source.TasksDataSource;
import com.evartem.remsimon.data.types.TasksManagerStarter;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.types.pinging.JavaPinger;
import com.evartem.remsimon.data.types.pinging.PingingTask;
import com.evartem.remsimon.util.AppExecutors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.evartem.remsimon.data.TasksManager.StateChangedListener.ADDED;
import static com.evartem.remsimon.data.TasksManager.StateChangedListener.DELETED;
import static com.evartem.remsimon.data.TasksManager.StateChangedListener.STATE_CHANGED;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TasksManagerImplTest {

    @Mock
    private TasksDataSource mockDataSource;

    @Mock
    private TasksManagerImpl.StateChangedListener stateChangedListener;

    @Mock
    private AppExecutors appExecutors;

    private TasksManager manager;

    private PingingTask TASK1 = PingingTask.create("TEST ping task 1", 1000, "127.0.0.1", 1000);

    private List<PingingTask> pingingTasks = Arrays.asList(
            PingingTask.create("TEST ping task 2", 1000, "127.0.0.2", 2000),
            PingingTask.create("TEST ping task 3", 1000, "127.0.0.3", 3000),
            PingingTask.create("TEST ping task 4", 1000, "127.0.0.4", 4000));

    @Before
    public void setUp() {

        TASK1.setPinger(new JavaPinger());
        pingingTasks.get(0).setPinger(new JavaPinger());
        pingingTasks.get(0).setJsonAdapter(AppModule.pingingTaskResultJsonAdapter(AppModule.moshi()));
        pingingTasks.get(1).setPinger(new JavaPinger());
        pingingTasks.get(1).setJsonAdapter(AppModule.pingingTaskResultJsonAdapter(AppModule.moshi()));
        pingingTasks.get(2).setPinger(new JavaPinger());
        pingingTasks.get(2).setJsonAdapter(AppModule.pingingTaskResultJsonAdapter(AppModule.moshi()));

        MockitoAnnotations.initMocks(this);

        when(appExecutors.mainThread()).thenReturn(Executors.newSingleThreadExecutor());
        when(appExecutors.diskIO()).thenReturn(Executors.newSingleThreadExecutor());

        TasksManagerStarter tasksManagerStarter = new TasksManagerImpl(mockDataSource, appExecutors, Executors.newFixedThreadPool(1));
        tasksManagerStarter.startManager();
        manager = tasksManagerStarter.getManager();
    }

    @After
    public void destroyRepositoryInstance() throws InterruptedException {
        manager.finish();
    }


    @Test
    public void addTasks_AddsOneTask() {
        // Given a task - TASK1
        // When a task is added to the manager
        manager.addTask(TASK1);

        // Then the task is added to to the data source
        verify(mockDataSource).updateOrAddTask(TASK1);
        // and to the manager's cache
        assertThat(manager.getCachedTasks().size(), is(1));
    }

    @Test
    public void addTasks_MultipleTasks() {
        // Given a few tasks
        // When they are added to the manager
        int numberOfTasks = addMultipleTasks();

        // Then they are added to the cache
        assertThat(manager.getCachedTasks().size(), is(numberOfTasks));
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
        assertThat(manager.getCachedTasks().size(), is(0));
    }

    @Test
    public void addTasks_SameTaskTwice() {
        // Given one task in the manager
        manager.addTask(TASK1);

        // When adding the same task again
        manager.addTask(TASK1);

        // Then the corresponding data source method is called only once
        verify(mockDataSource).updateOrAddTask(any());
        // and the cache size is still 1
        assertThat(manager.getCachedTasks().size(), is(1));
    }

    @Test
    public void getTasks_NoTasksReturnsEmptyList() {
        // Given no tasks in the manager
        // When requesting the cache
        List<MonitoringTask> tasks = manager.getCachedTasks();

        // Then an empty list is returned
        assertNotNull(tasks);
        assertThat(tasks.size(), is(0));
    }

    @Test
    public void deleteTask_OneTask() {
        // Given a few tasks in the manager
        addMultipleTasks();

        // When one of the tasks is deleted
        manager.deleteTask(TASK1);

        // Then make sure that task is gone from the cache
        assertFalse(manager.getCachedTasks().contains(TASK1));
        // and the corresponding method is called on the data source
        verify(mockDataSource).deleteTask(TASK1);
    }

    @Test
    public void deleteAllTasks() {
        // Given a few tasks in the manager
        addMultipleTasks();

        // When all the tasks are deleted
        manager.deleteAllTasks();

        // Then there's nothing in the cache
        assertThat(manager.getCachedTasks().size(), is(0));
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
    public void stateChangedListener_Added() {
        // Given no tasks in the manager
        // and the changes listener set
        manager.addTaskStateChangedListener(stateChangedListener);

        // When a few tasks were added
        addMultipleTasks();

        // Then the callback's method was called at least 2 times with the argument ADDED
        verify(stateChangedListener, timeout(200).atLeast(2)).onTaskStateChanged(any(MonitoringTask.class), anyInt(), eq(ADDED));
    }

    @Test
    public void setStateChangedListener_Changed() {
        // Given the changes listener set
        manager.addTaskStateChangedListener(stateChangedListener);
        // and a task in the manager
        PingingTask task = Mockito.mock(PingingTask.class);
        when(task.getTaskId()).thenReturn("TestTaskID");
        when(task.isTimeToExecute()).thenReturn(true).thenReturn(false);
        when(task.gotNewResult()).thenReturn(true);
        manager.addTask(task);

        // When the task is executed and its state changes

        // Then the callback was called with the arguments ADDED and CHANGED
        verify(stateChangedListener, timeout(500).times(1)).onTaskStateChanged(eq(task), anyInt(), eq(ADDED));
        verify(stateChangedListener, timeout(500).times(1)).onTaskStateChanged(eq(task), anyInt(), eq(STATE_CHANGED));
    }

    @Test
    public void setStateChangedListener_Deleted() {
        // Given the changes listener set
        manager.addTaskStateChangedListener(stateChangedListener);
        // and a task in the manager
        PingingTask task = Mockito.mock(PingingTask.class);
        when(task.getTaskId()).thenReturn("TestTaskID");
        when(task.isTimeToExecute()).thenReturn(false);
        manager.addTask(task);

        // When the task is deleted
        manager.deleteTask(task);

        // Then the callback was called with the arguments ADDED and DELETED
        verify(stateChangedListener, timeout(500).times(1)).onTaskStateChanged(eq(task), anyInt(), eq(ADDED));
        verify(stateChangedListener, timeout(500).times(1)).onTaskStateChanged(eq(task), anyInt(), eq(DELETED));
    }

    @Test
    public void taskRunsEveryMs() throws InterruptedException {
        // Given a task with the run period = 60 ms
        TASK1.setRunTaskEveryMs(60);
        PingingTask spyTASK1 = Mockito.spy(TASK1);

        // When it's added to the manager
        manager.addTask(spyTASK1);

        // Then it is run at least 2 times within 130 ms
        TimeUnit.MILLISECONDS.sleep(100);
        verify(spyTASK1, atLeast(2)).doTheWork();
    }

    @Test
    public void deactivatedTaskIsNotRun() throws InterruptedException {
        // Given a task
        TASK1.setRunTaskEveryMs(20);
        PingingTask spyTASK1 = Mockito.spy(TASK1);

        // When it's deactivated and added to the manager
        spyTASK1.deactivate();
        manager.addTask(spyTASK1);

        // Then it is not run by the manager
        TimeUnit.MILLISECONDS.sleep(60);
        verify(spyTASK1, never()).doTheWork();
    }

    /**
     * Convenience methods
     */

    private int addMultipleTasks() {
        manager.addTask(TASK1);
        manager.addTask(pingingTasks.get(0));
        manager.addTask(pingingTasks.get(1));
        manager.addTask(pingingTasks.get(2));
        return 4;
    }
}