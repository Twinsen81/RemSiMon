package com.evartem.remsimon.DI;

import android.app.Application;

import com.evartem.remsimon.DI.Scopes.PerApplication;
import com.evartem.remsimon.tasks.TasksActivity;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@PerApplication
@Component(modules = {
        //AppContextModule.class,
        TasksManagerModule.class,
        TasksContractModule.class})
public interface AppComponent {
    void inject(TasksActivity activity);

    @Component.Builder
    interface AppContextBuilder {
        AppComponent build();

        @BindsInstance
        AppContextBuilder setAppInstance(Application context);
    }

}
