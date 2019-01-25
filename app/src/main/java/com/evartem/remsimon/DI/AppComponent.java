package com.evartem.remsimon.DI;

import com.evartem.remsimon.DI.scopes.PerApplication;
import com.evartem.remsimon.TheApp;
import com.evartem.remsimon.data.types.http.HttpTask;
import com.evartem.remsimon.data.types.pinging.PingingTask;
import com.evartem.remsimon.tasks.UI.HttpViewHolder;
import com.evartem.remsimon.tasks.UI.PingingViewHolder;
import com.evartem.remsimon.tasks.UI.TaskViewHolder;

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

    void inject(PingingTask pingingTask);
    void inject(PingingViewHolder taskViewHolder);

    void inject(HttpTask httpTask);
    void inject(HttpViewHolder taskViewHolder);
}

