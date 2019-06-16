package com.example.bakingtime;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.bakingtime.UI.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mainTest = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setUpMainActivity() {

        mainTest.getActivity();

    }

    @Test
    public void recyclerViewTest() {
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));

        // To let the test work on the device you should first go to developer option on the app Settings
        // and under the Drawing section, switch all of the following options to Animation Off:
        // -Window animation scale
        // -Transition animation scale
        // -Animator duration scale
        // And the test will work. After the test, return the scale for the options to it's original value so the phone works as before.
        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

    }
}
