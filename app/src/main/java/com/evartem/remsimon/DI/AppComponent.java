package com.evartem.remsimon.DI;

import android.app.Application;

import com.evartem.remsimon.DI.scopes.PerApplication;
import com.evartem.remsimon.TheApp;
import com.evartem.remsimon.tasks.TasksActivity;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;

@PerApplication
@Component(modules = {
        AppModule.class,
        TasksManagerModule.class})
public interface AppComponent extends AndroidInjector<TheApp> {
    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<TheApp> {
    }
}

