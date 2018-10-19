package com.evartem.remsimon.DI;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.evartem.remsimon.DI.Scopes.PerApplication;
import com.evartem.remsimon.data.TasksManager;
import com.evartem.remsimon.data.TasksManagerImpl;
import com.evartem.remsimon.data.source.TasksDataSource;
import com.evartem.remsimon.data.source.local.TasksDatabase;
import com.evartem.remsimon.data.source.local.TasksLocalDataSource;
import com.evartem.remsimon.data.types.TasksManagerStarter;
import com.evartem.remsimon.util.AppExecutors;

import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
final class TasksManagerModule {

    @PerApplication
    @Provides
    static TasksManager getTasksManager(TasksDataSource dataSource, AppExecutors appExecutors) {
        TasksManagerStarter tasksManagerStarter = TasksManagerImpl.getInstance(dataSource, appExecutors, Executors.newFixedThreadPool(1));
        tasksManagerStarter.startManager();
        return tasksManagerStarter.getManager();
    }

    @Provides
    static AppExecutors getAppExecutors() {
        return new AppExecutors();
    }

    @PerApplication
    @Provides
    static TasksDataSource getLocalDataSource(AppExecutors appExecutors, TasksDatabase db) {
        return TasksLocalDataSource.getInstance(appExecutors, db.pingingTaskDao());
    }

    @PerApplication
    @Provides
    static TasksDatabase getTasksDatabase(Application context) {
        return Room.databaseBuilder(context, TasksDatabase.class, "RSM").build();
    }
}
