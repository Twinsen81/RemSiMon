package com.evartem.remsimon.tasks.ui;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.evartem.remsimon.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TasksActivityTest {

    @Rule
    public ActivityTestRule<TasksActivity> tasksActivityTestRule =
            new ActivityTestRule<>(TasksActivity.class);

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
        //onView(withId(R.id.add_task_title)).check(matches(isDisplayed()));
    }

/*    @Before
    public void registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }*/
}
