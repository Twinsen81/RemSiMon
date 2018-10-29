package com.evartem.remsimon;

import android.app.Activity;
import android.app.Application;
import android.os.Build;

import com.evartem.remsimon.DI.DaggerAppComponent;
import com.squareup.leakcanary.LeakCanary;
import net.danlew.android.joda.JodaTimeAndroid;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import timber.log.Timber;

public class TheApp extends Application implements HasActivityInjector {

    @Inject
    DispatchingAndroidInjector<Activity> activityInjector;

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        DaggerAppComponent.builder().create(this).inject(this);

        if (!isRobolectricUnitTest()) setupLeakCanary();

        JodaTimeAndroid.init(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }


    private void setupLeakCanary() {
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
