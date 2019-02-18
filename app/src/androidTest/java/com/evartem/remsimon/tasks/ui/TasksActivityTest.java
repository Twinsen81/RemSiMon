package com.evartem.remsimon.tasks.ui;

import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;

import com.evartem.remsimon.R;
import com.evartem.remsimon.TheApp;
import com.evartem.remsimon.data.types.base.TaskType;
import com.evartem.remsimon.di.AppComponent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.evartem.remsimon.tasks.ui.RecyclerViewMatcher.withRecyclerView;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TasksActivityTest {

    private static final String PINGING_TASK_DESCRIPTION = "Pinging_Task_Description";
    private static final String HTTP_TASK_DESCRIPTION = "HTTP_Task_Description";

    private static final String PINGING_NEW_PARAM_DESCRIPTION = "New_Pinging_Task_Description";
    private static final String PINGING_NEW_PARAM_ADDRESS = "10.10.10.10";
    private static final String PINGING_NEW_PARAM_RUN_EVERY = "99999";
    private static final String PINGING_NEW_PARAM_TIMEOUT = "888";
    private static final String PINGING_NEW_PARAM_ATTEMPTS = "7";

    private static final String HTTP_NEW_PARAM_DESCRIPTION = "New_HTTP_Task_Description";
    private static final String HTTP_NEW_PARAM_ADDRESS = "http://new.address.com";
    private static final String HTTP_NEW_PARAM_RUN_EVERY = "77777";
    private static final String HTTP_NEW_PARAM_FIELDS = "data1,data2";
    private static final String HTTP_NEW_PARAM_HISTORY = "9";


    @Rule
    public ActivityTestRule<TasksActivity> tasksActivityTestRule =
            new ActivityTestRule<>(TasksActivity.class);

    /**
     * Delete all previously created tasks for a clean test run
     */
    @Before
    public void init() {
        TheApp app = (TheApp) getInstrumentation().getTargetContext().getApplicationContext();
        AppComponent appComponent = app.getAppComponent();
        appComponent.getTasksManager().deleteAllTasks();
    }

    @Test
    public void clickAddPingingTaskButton_opensPingingTaskUi() {
        // When the FAB "Add task" button is clicked
        onView(withId(R.id.fab)).perform(click());

        // Then the the dialog window with task type selection is opened
        onView(withText(R.string.taskTypeDialogCaption))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        // When the "Pinging task" item is clicked
        onData(anything()).atPosition(1).perform(click());

        // Then the corresponding task editing fragment is opened
        onView(withId(R.id.tvAttempts)).check(matches(isDisplayed()));
    }

    @Test
    public void clickAddJSONTaskButton_opensJsonTaskUi() {
        // When the FAB "Add task" button is clicked
        onView(withId(R.id.fab)).perform(click());

        // Then the the dialog window with task type selection is opened
        onView(withText(R.string.taskTypeDialogCaption))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        // When the "JSON task" item is clicked in the dialog
        onData(anything()).atPosition(0).perform(click());

        // Then the corresponding task editing fragment is opened
        onView(withId(R.id.etFields)).check(matches(isDisplayed()));
    }

    @Test
    public void addNewPingingTask() {
        // When a task is added
        createTask(TaskType.PINGING_INT, PINGING_TASK_DESCRIPTION);

        // Then it appears in the Tasks list
        onView(withRecyclerView(R.id.rvTasks).atPosition(0))
                .check(matches(hasDescendant(withText(PINGING_TASK_DESCRIPTION))));
    }

    @Test
    public void addNewJsonTask() {
        // When a task is added
        createTask(TaskType.HTTP_INT, HTTP_TASK_DESCRIPTION);

        // Then it appears in the Tasks list
        onView(withRecyclerView(R.id.rvTasks).atPosition(0))
                .check(matches(hasDescendant(withText(HTTP_TASK_DESCRIPTION))));
    }

    @Test
    public void updatePingingTask() {
        // When a task is added
        createTask(TaskType.PINGING_INT, PINGING_TASK_DESCRIPTION);

        // clicked in the task list
        onView(withId(R.id.rvTasks)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // updated with new params
        onView(withId(R.id.etLabel)).perform(replaceText(PINGING_NEW_PARAM_DESCRIPTION), closeSoftKeyboard());
        onView(withId(R.id.etAddress)).perform(replaceText(PINGING_NEW_PARAM_ADDRESS), closeSoftKeyboard());
        onView(withId(R.id.etRunEveryMs)).perform(replaceText(PINGING_NEW_PARAM_RUN_EVERY), closeSoftKeyboard());
        onView(withId(R.id.etTimeoutMs)).perform(replaceText(PINGING_NEW_PARAM_TIMEOUT), closeSoftKeyboard());
        onView(withId(R.id.etAttempts)).perform(replaceText(PINGING_NEW_PARAM_ATTEMPTS), closeSoftKeyboard());

        // the changes applied
        onView(withId(R.id.btnApply)).perform(click());

        // the editing activity is opened again
        onView(withId(R.id.rvTasks)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Then the task's params should be equal to values entered
        onView(withId(R.id.etLabel)).check(matches(withText(PINGING_NEW_PARAM_DESCRIPTION)));
        onView(withId(R.id.etAddress)).check(matches(withText(PINGING_NEW_PARAM_ADDRESS)));
        onView(withId(R.id.etRunEveryMs)).check(matches(withText(PINGING_NEW_PARAM_RUN_EVERY)));
        onView(withId(R.id.etTimeoutMs)).check(matches(withText(PINGING_NEW_PARAM_TIMEOUT)));
        onView(withId(R.id.etAttempts)).check(matches(withText(PINGING_NEW_PARAM_ATTEMPTS)));
    }

    @Test
    public void updateJsonTask() {
        // When a task is added
        createTask(TaskType.HTTP_INT, HTTP_TASK_DESCRIPTION);

        // clicked in the task list
        onView(withId(R.id.rvTasks)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // updated with new params
        onView(withId(R.id.etLabel)).perform(replaceText(HTTP_NEW_PARAM_DESCRIPTION), closeSoftKeyboard());
        onView(withId(R.id.etAddress)).perform(replaceText(HTTP_NEW_PARAM_ADDRESS), closeSoftKeyboard());
        onView(withId(R.id.etRunEveryMs)).perform(replaceText(HTTP_NEW_PARAM_RUN_EVERY), closeSoftKeyboard());
        onView(withId(R.id.etFields)).perform(replaceText(HTTP_NEW_PARAM_FIELDS), closeSoftKeyboard());
        onView(withId(R.id.etTimeoutMs)).perform(replaceText(HTTP_NEW_PARAM_HISTORY), closeSoftKeyboard());

        // the changes applied
        onView(withId(R.id.btnApply)).perform(click());

        // the editing activity is opened again
        onView(withId(R.id.rvTasks)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Then the task's params should be equal to values entered
        onView(withId(R.id.etLabel)).check(matches(withText(HTTP_NEW_PARAM_DESCRIPTION)));
        onView(withId(R.id.etAddress)).check(matches(withText(HTTP_NEW_PARAM_ADDRESS)));
        onView(withId(R.id.etRunEveryMs)).check(matches(withText(HTTP_NEW_PARAM_RUN_EVERY)));
        onView(withId(R.id.etFields)).check(matches(withText(HTTP_NEW_PARAM_FIELDS)));
        onView(withId(R.id.etTimeoutMs)).check(matches(withText(HTTP_NEW_PARAM_HISTORY)));
    }

    @Test
    public void deletePingingTask() {
        // When a task is added
        createTask(TaskType.PINGING_INT, PINGING_TASK_DESCRIPTION);

        // Then it appears in the Tasks list
        onView(withRecyclerView(R.id.rvTasks).atPosition(0))
                .check(matches(hasDescendant(withText(PINGING_TASK_DESCRIPTION))));

        // When clicked in the task list
        onView(withId(R.id.rvTasks)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // and deleted
        onView(withId(R.id.btnDelete)).perform(click());

        // Then the task list is empty
        onView(withId(R.id.rvTasks)).check(hasItemsCount(0));
    }

    @Test
    public void deleteJsonTask() {
        // When a task is added
        createTask(TaskType.HTTP_INT, HTTP_TASK_DESCRIPTION);

        // Then it appears in the Tasks list
        onView(withRecyclerView(R.id.rvTasks).atPosition(0))
                .check(matches(hasDescendant(withText(HTTP_TASK_DESCRIPTION))));

        // When clicked in the task list
        onView(withId(R.id.rvTasks)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // and deleted
        onView(withId(R.id.btnDelete)).perform(click());

        // Then the task list is empty
        onView(withId(R.id.rvTasks)).check(hasItemsCount(0));
    }

     // Convenience methods

    /**
     * Check that the RecyclerView has certain number of items
     */
    public static ViewAssertion hasItemsCount(final int count) {
        return (view, e) -> {
            if (!(view instanceof RecyclerView))
                throw e;
            Assert.assertThat(((RecyclerView) view).getAdapter().getItemCount(), is(count));
        };
    }

    private void createTask(int taskType, String description) {
        // Click the FAB "Add task" button
        onView(withId(R.id.fab)).perform(click());

        // Select the proper task type
        onData(anything()).atPosition(taskType == TaskType.PINGING_INT ? 1 : 0).perform(click());

        // Enter the task's description
        onView(withId(R.id.etLabel)).perform(
                replaceText(description),
                closeSoftKeyboard());

        // Save the task (click "Apply")
        onView(withId(R.id.btnApply)).perform(click());
    }
}
