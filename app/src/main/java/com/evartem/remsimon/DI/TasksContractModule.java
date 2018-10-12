package com.evartem.remsimon.DI;

import com.evartem.remsimon.tasks.TasksContract;
import com.evartem.remsimon.tasks.TasksPresenter;

import dagger.Binds;
import dagger.Module;

@Module
abstract class TasksContractModule {

    @Binds
    abstract TasksContract.Presenter binds(TasksPresenter tasksPresenter);
}
