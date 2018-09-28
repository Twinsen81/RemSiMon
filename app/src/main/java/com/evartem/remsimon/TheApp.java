package com.evartem.remsimon;

import android.app.Application;
import android.os.Build;

import com.squareup.leakcanary.LeakCanary;
import net.danlew.android.joda.JodaTimeAndroid;

import timber.log.Timber;

public class TheApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        if (!isRobolectricUnitTest()) setupLeakCananry();

        JodaTimeAndroid.init(this);
    }

    private void setupLeakCananry() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    public static boolean isRobolectricUnitTest() {
        return "robolectric".equals(Build.FINGERPRINT);
    }
}
