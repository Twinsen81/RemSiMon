package com.evartem.remsimon.tasks.di;

import android.support.v7.app.AppCompatActivity;

import com.evartem.remsimon.di.base.BaseActivityModule;
import com.evartem.remsimon.di.scopes.PerActivity;
import com.evartem.remsimon.di.scopes.PerFragment;
import com.evartem.remsimon.taskEdit.http.di.HttpTaskEditFragmentModule;
import com.evartem.remsimon.taskEdit.http.HttpTaskEditFragment;
import com.evartem.remsimon.taskEdit.pinging.di.PingingTaskEditFragmentModule;
import com.evartem.remsimon.taskEdit.pinging.PingingTaskEditFragment;
import com.evartem.remsimon.tasks.ui.TasksActivity;
import com.evartem.remsimon.tasks.ui.TasksFragment;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module(includes = BaseActivityModule.class)
public abstract class TasksActivityModule {

    @PerFragment
    @ContributesAndroidInjector(modules = PingingTaskEditFragmentModule.class)
    abstract PingingTaskEditFragment pingingTaskEditFragmentInjector();

    @PerFragment
    @ContributesAndroidInjector(modules = HttpTaskEditFragmentModule.class)
    abstract HttpTaskEditFragment httpTaskEditFragmentInjector();

    @PerFragment
    @ContributesAndroidInjector(modules = TasksFragmentModule.class)
    abstract TasksFragment tasksFragmentInjector();

    @Binds
    @PerActivity
    abstract AppCompatActivity activity(TasksActivity activity);
}
