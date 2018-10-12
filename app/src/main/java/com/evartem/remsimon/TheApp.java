package com.evartem.remsimon;

import android.app.Application;
import android.os.Build;

import com.evartem.remsimon.DI.AppComponent;
import com.evartem.remsimon.DI.AppContextModule;
import com.evartem.remsimon.DI.DaggerAppComponent;
import com.squareup.leakcanary.LeakCanary;
import net.danlew.android.joda.JodaTimeAndroid;

import timber.log.Timber;

public class TheApp extends Application {

/*
    private static TasksManager tasksManager;
    public static TasksManager getTM() {
        return tasksManager;
    }
*/

    private static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

/*        TasksDatabase tasksDatabase = Room.databaseBuilder(this, TasksDatabase.class, "RSM").build();
        TasksLocalDataSource localDataSource = TasksLocalDataSource.getInstance(new AppExecutors(), tasksDatabase.pingingTaskDao());
        TasksManagerStarter tasksManagerStarter = TasksManagerImpl.getInstance(localDataSource, new AppExecutors(), Executors.newFixedThreadPool(1));
        tasksManagerStarter.startManager();
        tasksManager = tasksManagerStarter.getManager();*/

        component = DaggerAppComponent.builder()
                .appContextModule(new AppContextModule(this))
                .build();

        if (!isRobolectricUnitTest()) setupLeakCanary();

        JodaTimeAndroid.init(this);
    }

    public static AppComponent getComponent() {
        return component;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
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
