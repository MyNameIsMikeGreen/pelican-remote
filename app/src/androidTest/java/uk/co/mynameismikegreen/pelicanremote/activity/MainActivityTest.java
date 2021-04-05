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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import uk.co.mynameismikegreen.pelicanremote.R;
import uk.co.mynameismikegreen.pelicanremote.MockPelicanServerTestSuper;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends MockPelicanServerTestSuper {

    public static final String BAD_ADDRESS = "192.0.2.0";

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void notConnectedToServer() throws InterruptedException {
        // Given: App is configured to use a non-existent server
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Editor preferencesEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        preferencesEditor.putString("address", BAD_ADDRESS);
        preferencesEditor.commit();

        // When: The activity is launched
        Thread.sleep(1000);

        // Then: All buttons are visible but not enabled
        assertButtonState(R.id.activateButton, true, false);
        assertButtonState(R.id.activateUntilMidnightButton, true, false);
        assertButtonState(R.id.deactivateButton, true, false);
        assertButtonState(R.id.rescanButton, true, false);
    }

    @Test
    public void deactivatedServer() throws InterruptedException {
        // Given: App is launched
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Editor preferencesEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        // And: App is configured to use a deactivated server
        setPelicanStub("/status", readResource("status_response_deactivated.json"));
        preferencesEditor.putString("address", getMockPelicanServerHost());
        preferencesEditor.putString("port", String.valueOf(getMockPelicanServerPort()));
        preferencesEditor.commit();

        // When: The activity is launched
        Thread.sleep(3000);

        // Then: Only activation buttons are enabled
        assertButtonState(R.id.activateButton, true, true);
        assertButtonState(R.id.activateUntilMidnightButton, true, true);
        assertButtonState(R.id.deactivateButton, true, false);
        assertButtonState(R.id.rescanButton, true, false);

    }

    private void assertButtonState(int buttonId, boolean visible, boolean enabled) {
        Matcher<View> visibleMatcher = visible ? isDisplayed() : not(isDisplayed());
        Matcher<View> enabledMatcher = enabled ? isEnabled() : not(isEnabled());
        onView(withId(buttonId)).check(matches(visibleMatcher));
        onView(withId(buttonId)).check(matches(enabledMatcher));
    }

    private String readResource(String path) {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(path);
        return new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

    }

}