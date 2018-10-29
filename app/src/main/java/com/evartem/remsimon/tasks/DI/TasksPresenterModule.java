package com.evartem.remsimon.tasks.DI;

import com.evartem.remsimon.DI.scopes.PerFragment;
import com.evartem.remsimon.tasks.ContractMVP.TasksPresenter;
import com.evartem.remsimon.tasks.TasksPresenterImpl;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class  TasksPresenterModule {
    @Binds
    @PerFragment
    abstract TasksPresenter example1Presenter(TasksPresenterImpl example1PresenterImpl);
}
