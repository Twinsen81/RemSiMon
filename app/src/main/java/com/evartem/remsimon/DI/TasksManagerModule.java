package com.evartem.remsimon.DI;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.evartem.remsimon.DI.scopes.PerApplication;
import com.evartem.remsimon.data.TasksManager;
import com.evartem.remsimon.data.TasksManagerImpl;
import com.evartem.remsimon.data.source.TasksDataSource;
import com.evartem.remsimon.data.source.local.HttpTaskDao;
import com.evartem.remsimon.data.source.local.PingingTaskDao;
import com.evartem.remsimon.data.source.local.TasksDatabase;
import com.evartem.remsimon.data.source.local.TasksLocalDataSource;
import com.evartem.remsimon.data.types.TasksManagerStarter;
import com.evartem.remsimon.util.AppExecutors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
abstract class TasksManagerModule {

    @PerApplication
    @Provides
    static TasksManager getTasksManager(TasksManagerStarter tasksManagerStarter) {
        tasksManagerStarter.startManager();
        return tasksManagerStarter.getManager();
    }

    @PerApplication
    @Binds
    abstract TasksManagerStarter getTasksManagerStarter(TasksManagerImpl tasksManagerImpl);

    @PerApplication
    @Named("managerThreadExecutor")
    @Provides
    static ExecutorService managerThreadExecutor() {
        return Executors.newFixedThreadPool(1);
    }

    @Provides
    static AppExecutors getAppExecutors() {
        return new AppExecutors();
    }

    @PerApplication
    @Binds
    abstract TasksDataSource getLocalDataSource(TasksLocalDataSource localDataSource);

    @PerApplication
    @Provides
    static PingingTaskDao getPingingTaskDao(TasksDatabase db) {
        return db.pingingTaskDao();
    }

    @PerApplication
    @Provides
    static HttpTaskDao getHttpTaskDao(TasksDatabase db) {
        return db.httpTaskDao();
    }

    @PerApplication
    @Provides
    static TasksDatabase getTasksDatabase(Application context) {
        return Room.databaseBuilder(context, TasksDatabase.class, "RSM").build();
    }
}
