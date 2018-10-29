package com.evartem.remsimon.tasks.DI;

import android.app.Fragment;

import com.evartem.remsimon.DI.base.BaseFragmentModule;
import com.evartem.remsimon.DI.scopes.PerFragment;
import com.evartem.remsimon.tasks.ContractMVP.TasksView;
import com.evartem.remsimon.tasks.TasksFragment;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;

@Module(includes = {BaseFragmentModule.class, TasksPresenterModule.class})
public abstract class TasksFragmentModule {

    @Binds
    @Named(BaseFragmentModule.FRAGMENT)
    @PerFragment
    abstract Fragment fragment(TasksFragment fragment);

    @Binds
    @PerFragment
    abstract TasksView tasksView(TasksFragment tasksFragment);
}