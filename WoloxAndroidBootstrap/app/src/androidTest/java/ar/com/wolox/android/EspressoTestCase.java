package ar.com.wolox.android;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.test.ActivityInstrumentationTestCase2;

/**
 * This requires to be declared in the effective manifest:
 * <uses-permission android:name="android.permission.WAKE_LOCK"/>
 *
 * If you are using Gradle and don't want this permission it in your main application
 * you can decleare this in a debug build config manifest (ex: app/src/debug/AndroidManifest.xml).
 */
public abstract class EspressoTestCase<T extends Activity> extends ActivityInstrumentationTestCase2<T> {
    private final long DEFAULT_WAKE_LOCK_DURATION = 30 * 1000; // 30 seconds

    private WakeLock wakeLock;

    protected EspressoTestCase(Class<T> activityClass) {
        super(activityClass);
    }

    /**
     * By default the test case will keep the device awake for at least 30 seconds.
     * If you need a different period of time you can override this method.
     */
    protected long keepDeviceAwakeForAtLeastMilliseconds() {
        return DEFAULT_WAKE_LOCK_DURATION;
    }

    /**
     * This **must** be called by implementations if they override setUp().
     */
    @Override
    @SuppressWarnings("deprecation")
    public void setUp() throws Exception {
        super.setUp();

        // Espresso will not launch our activity for us, we must launch it via getActivity().
        getActivity();

        // Addresses an issue where if the screen is off or locked Espresso will fail with this error:
        //   com.google.android.apps.common.testing.ui.espresso.NoActivityResumedException: No activities in stage RESUMED. Did you forget to launch the activity. (test.getActivity() or similar)?
        // The API docs recommend using LayoutParams.FLAG_TURN_SCREEN_ON, etc, but by the time we
        // have a chance to set them Activity.onCreate() has already been called and the flags will
        // not have an effect on the activity (they must be set prior to calling
        PowerManager pm = (PowerManager) getInstrumentation().getTargetContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, EspressoTestCase.class.getSimpleName());
        wakeLock.acquire(keepDeviceAwakeForAtLeastMilliseconds());
    }

    /**
     * This **must** be called by implementations if they override tearDown().
     */
    @Override
    protected void tearDown() throws Exception {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }

        super.tearDown();
    }

    protected void runOnUiThread(final UIThreadRunner<T> runner) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                runner.onUiThread(getActivity());
            }
        });
    }

    public interface UIThreadRunner<T> {
        public void onUiThread(T activity);
    }
}