package com.evartem.remsimon.DI;

import android.app.Application;

import com.evartem.remsimon.DI.scopes.PerApplication;
import com.evartem.remsimon.TheApp;
import com.evartem.remsimon.data.types.http.GeneralApi;
import com.evartem.remsimon.data.types.http.HttpTask;
import com.evartem.remsimon.tasks.TasksActivity;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import retrofit2.Retrofit;

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

    //GeneralApi generalApi();
    void inject(HttpTask httpTask);
}

