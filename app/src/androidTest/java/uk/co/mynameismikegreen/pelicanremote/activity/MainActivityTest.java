package uk.co.mynameismikegreen.pelicanremote.activity;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.mynameismikegreen.pelicanremote.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void allButtonsAreVisible() {
        onView(withId(R.id.activateButton)).check(matches(isDisplayed()));
        onView(withId(R.id.activateUntilMidnightButton)).check(matches(isDisplayed()));
        onView(withId(R.id.deactivateButton)).check(matches(isDisplayed()));
        onView(withId(R.id.rescanButton)).check(matches(isDisplayed()));
    }

}