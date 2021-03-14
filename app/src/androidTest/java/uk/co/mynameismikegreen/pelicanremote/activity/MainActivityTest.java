package uk.co.mynameismikegreen.pelicanremote.activity;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.view.View;

import androidx.preference.PreferenceManager;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;


import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import uk.co.mynameismikegreen.pelicanremote.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void allButtonsAreVisible() {
        // Given: App is configured to use a non-existent server
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Editor preferencesEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        preferencesEditor.putString("address", "192.111.1.111");
        preferencesEditor.commit();

        // When: The activity is launched

        // Then: All buttons are visible but not enabled
        assertButtonState(R.id.activateButton, true, false);
        assertButtonState(R.id.activateUntilMidnightButton, true, false);
        assertButtonState(R.id.deactivateButton, true, false);
        assertButtonState(R.id.rescanButton, true, false);
    }

    private void assertButtonState(int buttonId, boolean visible, boolean enabled) {
        Matcher<View> visibleMatcher = visible ? isDisplayed() : not(isDisplayed());
        Matcher<View> enabledMatcher = enabled ? isEnabled() : not(isEnabled());
        onView(withId(buttonId)).check(matches(visibleMatcher));
        onView(withId(buttonId)).check(matches(enabledMatcher));
    }

}