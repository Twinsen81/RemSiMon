package com.evartem.remsimon.DI;

import android.app.Application;

import com.evartem.remsimon.DI.scopes.PerActivity;
import com.evartem.remsimon.DI.scopes.PerApplication;
import com.evartem.remsimon.TheApp;
import com.evartem.remsimon.tasks.TasksActivity;
import com.evartem.remsimon.tasks.DI.TasksActivityModule;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjectionModule;
import dagger.android.ContributesAndroidInjector;

@Module(includes = AndroidInjectionModule.class)
abstract class AppModule {

    @Binds
    @PerApplication
    abstract Application application(TheApp app);

    @PerActivity
    @ContributesAndroidInjector(modules = TasksActivityModule.class)
    abstract TasksActivity tasksActivityInjector();

}
