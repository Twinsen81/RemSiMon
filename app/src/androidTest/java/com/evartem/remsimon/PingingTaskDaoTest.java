package com.evartem.remsimon;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.evartem.remsimon.data.source.local.TasksDatabase;
import com.evartem.remsimon.data.types.pinging.PingingTask;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PingingTaskDaoTest {

    TasksDatabase database;
    PingingTask TASK1, TASK2;

    private static final String TASK1_DESCRIPTION = "Test task description";
    private static final String TASK1_PING_ADDRESS = "8.8.8.8";  // google's dns
    private static final int TASK1_RUN_EVERY = 3;
    private static final int TASK1_PING_TIMEOUT = 300;

    private static final String TASK2_DESCRIPTION = "Test task - non-existent IP ";
    private static final String TASK2_PING_ADDRESS = "192.168.88.88";
    private static final int TASK2_RUN_EVERY = 2;
    private static final int TASK2_PING_TIMEOUT = 900;


    @Before
    public void init() {
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), TasksDatabase.class).build();
        TASK1 = PingingTask.create(TASK1_DESCRIPTION, TASK1_RUN_EVERY, TASK1_PING_ADDRESS, TASK1_PING_TIMEOUT);
        TASK2 = PingingTask.create(TASK2_DESCRIPTION, TASK2_RUN_EVERY, TASK2_PING_ADDRESS, TASK2_PING_TIMEOUT);
    }

    @After
    public void closeDb() {
        database.close();
    }

    @Test
    public void insertTask_getById() {
        // Given a task in the Db
        database.pingingTaskDao().addOrReplace(TASK1);

        // When the task is requested by it's ID
        PingingTask taskFromDb = database.pingingTaskDao().getById(TASK1.getTaskId());

        // The same task is returned from the Db
        assertEqualsTestTask(taskFromDb, TASK1);
    }

    @Test
    public void addMultipleTasksByOne() {
        // Given two tasks in the Db
        database.pingingTaskDao().addOrReplace(TASK1);
        database.pingingTaskDao().addOrReplace(TASK2);

        // When requesting all the tasks from the Db
        List<PingingTask> tasks = database.pingingTaskDao().getAll();

        // Then the tasks that were added previously are returned
        assertThat(tasks.size(), is(2));
        assertExistsAndEqualsTestTask(tasks, TASK1);
        assertExistsAndEqualsTestTask(tasks, TASK2);
    }

    @Test
    public void addMultipleTasks() {
        // Given two tasks
        List<PingingTask> tasks = new ArrayList<>();
        tasks.add(TASK1);
        tasks.add(TASK2);

        // When they're added to the DB
        database.pingingTaskDao().addOrReplaceAll(tasks);

        // Then the Db has 2 entries
        List<PingingTask> dbTasks = database.pingingTaskDao().getAll();
        assertThat(dbTasks.size(), is(2));

        // and those are the previously added tasks
        assertExistsAndEqualsTestTask(tasks, TASK1);
        assertExistsAndEqualsTestTask(tasks, TASK2);
    }

    @Test
    public void replaceExistingTask() {
        // Given a task in the Db
        database.pingingTaskDao().addOrReplace(TASK1);

        // When the task is added with the same ID
        TASK2.setTaskId(TASK1.getTaskId());
        database.pingingTaskDao().addOrReplace(TASK2);

        // Then the first task is overwritten by the second
        List<PingingTask> tasks = database.pingingTaskDao().getAll();
        assertThat(tasks.size(), is(1));
        assertThat(tasks.get(0).getTaskId(), is(TASK2.getTaskId()));
        assertThat(tasks.get(0).getDescription(), is(TASK2.getDescription()));
    }

    @Test
    public void replaceMultipleTasks() {
        // Given two tasks
        List<PingingTask> tasks = new ArrayList<>();
        tasks.add(TASK1);
        tasks.add(TASK2);

        // When they're added to the DB
        database.pingingTaskDao().addOrReplaceAll(tasks);
        // and changed
        final String NEW_DESCRIPTION1 = "CHANGED1";
        final String NEW_DESCRIPTION2 = "CHANGED2";
        TASK1.setDescription(NEW_DESCRIPTION1);
        TASK2.setDescription(NEW_DESCRIPTION2);
        // and added again
        database.pingingTaskDao().addOrReplaceAll(tasks);

        // Then there's still only 2 entries in the DB
        List<PingingTask> dbTasks = database.pingingTaskDao().getAll();
        assertThat(dbTasks.size(), is(2));
        // and the changes were applied to them
        assertThat(database.pingingTaskDao().getById(TASK1.getTaskId()).getDescription(), is(NEW_DESCRIPTION1));
        assertThat(database.pingingTaskDao().getById(TASK2.getTaskId()).getDescription(), is(NEW_DESCRIPTION2));
    }

    @Test
    public void deleteById() {
        // Given two tasks in the Db
        database.pingingTaskDao().addOrReplace(TASK1);
        database.pingingTaskDao().addOrReplace(TASK2);

        // When a task is deleted
        int numberDeleted = database.pingingTaskDao().deleteById(TASK1.getTaskId());

        // Then number of deleted entries == 1
        assertThat(numberDeleted, is(1));
        // and there's only 1 entry left in the DB
        assertThat(database.pingingTaskDao().getAll().size(), is(1));
        // and the right one was deleted
        assertNull(database.pingingTaskDao().getById(TASK1.getTaskId()));
    }

    @Test
    public void deleteAll() {
        // Given two tasks in the Db
        database.pingingTaskDao().addOrReplace(TASK1);
        database.pingingTaskDao().addOrReplace(TASK2);

        // When all tasks are deleted from the Db
        int numberDeleted = database.pingingTaskDao().deleteAll();

        // Then number of deleted entries == 2
        assertThat(numberDeleted, is(2));
        // and there's nothing left in the DB
        assertThat(database.pingingTaskDao().getAll().size(), is (0));
    }


    /**
     * Convenience methods
     */

    private void assertExistsAndEqualsTestTask(List<PingingTask> tasks, PingingTask testTask) {
        PingingTask task = findTask(testTask.getTaskId(), tasks);
        assertNotNull(task);
        assertEqualsTestTask(task, testTask);
    }

    private void assertEqualsTestTask(PingingTask taskToTest, PingingTask testTask) {
        assertThat(taskToTest.getTaskId(), is(testTask.getTaskId()));
        assertThat(taskToTest.getDescription(), is(testTask.getDescription()));
        assertThat(taskToTest.getRunTaskEveryNSeconds(), is(testTask.getRunTaskEveryNSeconds()));
        assertThat(taskToTest.settings.getPingAddress(), is(testTask.settings.getPingAddress()));
        assertThat(taskToTest.settings.getPingTimeoutMs(), is(testTask.settings.getPingTimeoutMs()));
    }

    private PingingTask findTask(String taskId, List<PingingTask> tasks) {
        for (PingingTask task :
                tasks) {
            if (task.getTaskId().equals(taskId)) return task;
        }
        return null;
    }
}
