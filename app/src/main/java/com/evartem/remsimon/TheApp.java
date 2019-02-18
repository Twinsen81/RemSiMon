package com.evartem.remsimon;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;

import com.evartem.remsimon.di.AppComponent;
import com.evartem.remsimon.di.DaggerAppComponent;
import com.evartem.remsimon.util.ConnectivityChangeReceiver;
import com.squareup.leakcanary.LeakCanary;

import net.danlew.android.joda.JodaTimeAndroid;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.DaggerApplication;
import timber.log.Timber;

public class TheApp extends DaggerApplication {

    public static volatile boolean isInternetConnectionAvailable = false;

    @Inject
    DispatchingAndroidInjector<Activity> activityInjector;
    private AppComponent appComponent;


    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        if (!isRobolectricUnitTest()) setupLeakCanary();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new ConnectivityChangeReceiver(), intentFilter);

        JodaTimeAndroid.init(this);
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

    public static synchronized boolean isRunningEspressoTest() {
        boolean isRunningEspressoTest;

        try {
            Class.forName("android.support.test.espresso.Espresso");
            isRunningEspressoTest = true;
        } catch (ClassNotFoundException e) {
            isRunningEspressoTest = false;
        }

        return isRunningEspressoTest;
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        AndroidInjector<TheApp> ai = DaggerAppComponent.builder().create(this);
        appComponent = (AppComponent) ai;
        return ai;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

}
