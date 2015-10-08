package ar.com.wolox.android;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.test.ActivityInstrumentationTestCase2;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ar.com.wolox.android.activity.MainActivity;

/**
 * Created by framundo on 10/8/15.
 */
public class MainActivityTest extends EspressoTestCase<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Test
    public void testCheckText() {
        onView(withText("holis")).check(matches(isDisplayed()));
    }
}
