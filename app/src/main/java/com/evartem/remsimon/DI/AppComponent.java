package com.evartem.remsimon.DI;

import com.evartem.remsimon.DI.scopes.PerApplication;
import com.evartem.remsimon.TheApp;
import com.evartem.remsimon.data.types.http.HttpTask;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@PerApplication
@Component(modules = {
        AppModule.class,
        AndroidSupportInjectionModule.class,
        TasksManagerModule.class,
        RetrofitModule.class})
public interface AppComponent extends AndroidInjector<TheApp> {
    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<TheApp> {
    }

    void inject(HttpTask httpTask);
}

