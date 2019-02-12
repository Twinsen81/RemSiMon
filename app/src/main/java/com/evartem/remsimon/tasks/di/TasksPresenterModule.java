package com.evartem.remsimon.tasks.di;

import com.evartem.remsimon.di.scopes.PerFragment;
import com.evartem.remsimon.tasks.contractmvp.TasksPresenter;
import com.evartem.remsimon.tasks.TasksPresenterImpl;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class  TasksPresenterModule {
    @Binds
    @PerFragment
    abstract TasksPresenter tasksPresenter(TasksPresenterImpl tasksPresenterImpl);
}
