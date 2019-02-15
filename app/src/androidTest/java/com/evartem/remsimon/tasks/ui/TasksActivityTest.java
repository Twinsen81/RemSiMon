package com.evartem.remsimon.tasks.ui;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.evartem.remsimon.R;
import com.evartem.remsimon.TheApp;
import com.evartem.remsimon.data.types.base.TaskType;
import com.evartem.remsimon.di.AppComponent;

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

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TasksActivityTest {

    private static final String PINGING_TASK_DESCRIPTION = "Pinging_Task_Description";
    private static final String HTTP_TASK_DESCRIPTION = "HTTP_Task_Description";
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

        // When the "JSON task" item is clicked
        onData(anything()).atPosition(0).perform(click());

        // Then the corresponding task editing fragment is opened
        onView(withId(R.id.etFields)).check(matches(isDisplayed()));
    }

    @Test
    public void addNewPingingTask() {
        createTask(TaskType.PINGING_INT, PINGING_TASK_DESCRIPTION);

        onView(withRecyclerView(R.id.rvTasks).atPosition(0))
                .check(matches(hasDescendant(withText(PINGING_TASK_DESCRIPTION))));
    }

    @Test
    public void addNewJsonTask() {
        createTask(TaskType.HTTP_INT, HTTP_TASK_DESCRIPTION);

        onView(withRecyclerView(R.id.rvTasks).atPosition(0))
                .check(matches(hasDescendant(withText(HTTP_TASK_DESCRIPTION))));
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
