package com.evartem.remsimon.DI;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.evartem.remsimon.data.TasksManager;
import com.evartem.remsimon.data.TasksManagerImpl;
import com.evartem.remsimon.data.source.local.TasksDatabase;
import com.evartem.remsimon.data.source.local.TasksLocalDataSource;
import com.evartem.remsimon.data.types.TasksManagerStarter;
import com.evartem.remsimon.util.AppExecutors;

import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

@Module
public class TasksManagerModule {

    TasksManagerImpl tasksManager;

    // @Inject
    public TasksManagerModule(TasksManagerImpl tasksManagerImpl) {
        tasksManager = tasksManagerImpl;

        TasksDatabase tasksDatabase =
        TasksLocalDataSource localDataSource = TasksLocalDataSource.getInstance(new AppExecutors(), tasksDatabase.pingingTaskDao());
        TasksManagerStarter tasksManagerStarter = TasksManagerImpl.getInstance(localDataSource, new AppExecutors(), Executors.newFixedThreadPool(1));
        tasksManagerStarter.startManager();
        tasksManager = tasksManagerStarter.getManager();
    }

    @Provides
    TasksLocalDataSource getLocalDataSource(AppExecutors appExecutors, TasksDatabase db) {
        return TasksLocalDataSource.getInstance(appExecutors, db.pingingTaskDao());
    }


    @Provides
    TasksDatabase getTasksDatabase(Application context) {
        return Room.databaseBuilder(context, TasksDatabase.class, "RSM").build();
    }

    @Provides
    TasksManager getTasksManager() {
        return tasksManager;
    }
}
