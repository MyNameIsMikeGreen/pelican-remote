package uk.co.mynameismikegreen.pelicanremote;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
public class ApplicationTest {

    @Test
    public void packageIsCorrect() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("uk.co.mynameismikegreen.pelicanremote", appContext.getPackageName());
    }

}
