package com.evartem.remsimon.DI;

import android.arch.persistence.room.Ignore;

import com.evartem.remsimon.DI.scopes.PerApplication;
import com.evartem.remsimon.TheApp;
import com.evartem.remsimon.data.types.http.HttpTask;
import com.evartem.remsimon.data.types.pinging.PingingTask;
import com.evartem.remsimon.data.types.pinging.PingingTaskResult;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import dagger.Component;
import dagger.Provides;
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
    void inject(PingingTask pingingTask);
}

