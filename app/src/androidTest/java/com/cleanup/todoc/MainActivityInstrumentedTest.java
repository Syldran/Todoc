package com.cleanup.todoc;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.cleanup.todoc.RecyclerViewItemCountAssertion.withItemCount;
import static com.cleanup.todoc.TestUtils.withRecyclerView;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.cleanup.todoc.ui.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @author Gaëtan HERFRAY
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4.class)
public class MainActivityInstrumentedTest {
    @Rule
    //public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);
        private ActivityScenario scenario;

    @Before
    public void setUp() {
        scenario = activityScenarioRule.getScenario().moveToState(Lifecycle.State.RESUMED);
        assertThat(scenario, notNullValue());
    }

    @Test
    public void addAndRemoveTask() {

            onView(withId(R.id.fab_add_task)).perform(click());
            onView(withId(R.id.txt_task_name)).perform(replaceText("Tâche example"));
            onView(withId(android.R.id.button1)).perform(click());

            // Check that lblTask is not displayed anymore
            onView(withId(R.id.lbl_task_name)).check(matches(isDisplayed()));
            // Check that recyclerView is displayed
            onView(withId(R.id.list_tasks)).check(matches(isDisplayed()));
            // Check that it contains one element only
            onView(ViewMatchers.withId(R.id.list_tasks)).check(withItemCount(1));

            onView(withId(R.id.img_delete)).perform(click());

            // Check that lblTask is displayed
        onView(withId(R.id.lbl_task_name)).check(matches(not(isDisplayed())));

            // Check that recyclerView is not displayed anymore
        onView(withId(R.id.list_tasks)).check(matches(not(isDisplayed())));
          //  assertThat(listTasks.getVisibility(), equalTo(View.GONE));

    }

    @Test
    public void sortTasks() {


            onView(withId(R.id.fab_add_task)).perform(click());
            onView(withId(R.id.txt_task_name)).perform(replaceText("aaa Tâche example"));
            onView(withId(android.R.id.button1)).perform(click());
            onView(withId(R.id.fab_add_task)).perform(click());
            onView(withId(R.id.txt_task_name)).perform(replaceText("zzz Tâche example"));
            onView(withId(android.R.id.button1)).perform(click());
            onView(withId(R.id.fab_add_task)).perform(click());
            onView(withId(R.id.txt_task_name)).perform(replaceText("hhh Tâche example"));
            onView(withId(android.R.id.button1)).perform(click());

            // Sort alphabetical
            onView(withId(R.id.action_filter)).perform(click());
            onView(withText(R.string.sort_alphabetical)).perform(click());
            onView(withRecyclerView(R.id.list_tasks).atPositionOnView(0, R.id.lbl_task_name))
                    .check(matches(withText("aaa Tâche example")));
            onView(withRecyclerView(R.id.list_tasks).atPositionOnView(1, R.id.lbl_task_name))
                    .check(matches(withText("hhh Tâche example")));
            onView(withRecyclerView(R.id.list_tasks).atPositionOnView(2, R.id.lbl_task_name))
                    .check(matches(withText("zzz Tâche example")));

            // Sort alphabetical inverted
            onView(withId(R.id.action_filter)).perform(click());
            onView(withText(R.string.sort_alphabetical_invert)).perform(click());
            onView(withRecyclerView(R.id.list_tasks).atPositionOnView(0, R.id.lbl_task_name))
                    .check(matches(withText("zzz Tâche example")));
            onView(withRecyclerView(R.id.list_tasks).atPositionOnView(1, R.id.lbl_task_name))
                    .check(matches(withText("hhh Tâche example")));
            onView(withRecyclerView(R.id.list_tasks).atPositionOnView(2, R.id.lbl_task_name))
                    .check(matches(withText("aaa Tâche example")));

            // Sort old first
            onView(withId(R.id.action_filter)).perform(click());
            onView(withText(R.string.sort_oldest_first)).perform(click());
            onView(withRecyclerView(R.id.list_tasks).atPositionOnView(0, R.id.lbl_task_name))
                    .check(matches(withText("aaa Tâche example")));
            onView(withRecyclerView(R.id.list_tasks).atPositionOnView(1, R.id.lbl_task_name))
                    .check(matches(withText("zzz Tâche example")));
            onView(withRecyclerView(R.id.list_tasks).atPositionOnView(2, R.id.lbl_task_name))
                    .check(matches(withText("hhh Tâche example")));

            // Sort recent first
            onView(withId(R.id.action_filter)).perform(click());
            onView(withText(R.string.sort_recent_first)).perform(click());
            onView(withRecyclerView(R.id.list_tasks).atPositionOnView(0, R.id.lbl_task_name))
                    .check(matches(withText("hhh Tâche example")));
            onView(withRecyclerView(R.id.list_tasks).atPositionOnView(1, R.id.lbl_task_name))
                    .check(matches(withText("zzz Tâche example")));
            onView(withRecyclerView(R.id.list_tasks).atPositionOnView(2, R.id.lbl_task_name))
                    .check(matches(withText("aaa Tâche example")));

    }
}
