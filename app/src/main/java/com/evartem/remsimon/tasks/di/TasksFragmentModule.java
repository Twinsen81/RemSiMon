package com.evartem.remsimon.tasks.di;


import android.support.v4.app.Fragment;

import com.evartem.remsimon.di.base.BaseFragmentModule;
import com.evartem.remsimon.di.scopes.PerFragment;
import com.evartem.remsimon.tasks.contractmvp.TasksView;
import com.evartem.remsimon.tasks.ui.TasksFragment;

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