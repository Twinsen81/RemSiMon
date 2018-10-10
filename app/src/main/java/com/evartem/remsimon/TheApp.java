package com.evartem.remsimon;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.os.Build;

import com.evartem.remsimon.data.TasksManager;
import com.evartem.remsimon.data.TasksManagerImpl;
import com.evartem.remsimon.data.source.local.TasksDatabase;
import com.evartem.remsimon.data.source.local.TasksLocalDataSource;
import com.evartem.remsimon.data.types.TasksManagerStarter;
import com.evartem.remsimon.util.AppExecutors;
import com.squareup.leakcanary.LeakCanary;
import net.danlew.android.joda.JodaTimeAndroid;

import java.util.concurrent.Executors;

import timber.log.Timber;

public class TheApp extends Application {

    private static TasksManager tasksManager;

    public static TasksManager getTM() {
        return tasksManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

/*        TasksDatabase tasksDatabase = Room.databaseBuilder(this, TasksDatabase.class, "RSM").build();
        TasksLocalDataSource localDataSource = TasksLocalDataSource.getInstance(new AppExecutors(), tasksDatabase.pingingTaskDao());
        TasksManagerStarter tasksManagerStarter = TasksManagerImpl.getInstance(localDataSource, new AppExecutors(), Executors.newFixedThreadPool(1));
        tasksManagerStarter.startManager();
        tasksManager = tasksManagerStarter.getManager();*/


        if (!isRobolectricUnitTest()) setupLeakCananry();

        JodaTimeAndroid.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
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
