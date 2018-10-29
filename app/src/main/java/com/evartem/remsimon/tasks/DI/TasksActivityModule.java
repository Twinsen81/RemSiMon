package com.evartem.remsimon.tasks.DI;

import android.app.Activity;

import com.evartem.remsimon.DI.base.BaseActivityModule;
import com.evartem.remsimon.DI.scopes.PerActivity;
import com.evartem.remsimon.DI.scopes.PerFragment;
import com.evartem.remsimon.tasks.TasksActivity;
import com.evartem.remsimon.tasks.TasksFragment;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module(includes = BaseActivityModule.class)
public abstract class TasksActivityModule {


    @PerFragment
    @ContributesAndroidInjector(modules = TasksFragmentModule.class)
    abstract TasksFragment tasksFragmentInjector();

    @Binds
    @PerActivity
    abstract Activity activity(TasksActivity activity);

/*    @Binds
    @PerActivity
    abstract MainFragmentListener mainFragmentListener(MainActivity mainActivity);*/
}
