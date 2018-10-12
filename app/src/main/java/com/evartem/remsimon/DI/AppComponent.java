package com.evartem.remsimon.DI;

import com.evartem.remsimon.tasks.TasksActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        AppContextModule.class,
        TasksManagerModule.class,
        TasksContractModule.class})
public interface AppComponent {
    void inject(TasksActivity activity);
}
