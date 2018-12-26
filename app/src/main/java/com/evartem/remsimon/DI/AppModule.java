package com.evartem.remsimon.DI;

import android.app.Application;

import com.evartem.remsimon.DI.scopes.PerActivity;
import com.evartem.remsimon.DI.scopes.PerApplication;
import com.evartem.remsimon.TheApp;
import com.evartem.remsimon.data.types.http.HttpTaskResult;
import com.evartem.remsimon.data.types.pinging.HybridPinger;
import com.evartem.remsimon.data.types.pinging.Pinger;
import com.evartem.remsimon.data.types.pinging.PingingTaskResult;
import com.evartem.remsimon.tasks.DI.TasksActivityModule;
import com.evartem.remsimon.tasks.TasksActivity;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Module(includes = AndroidSupportInjectionModule.class)
public abstract class AppModule {

    @Binds
    @PerApplication
    abstract Application application(TheApp app);

    @PerActivity
    @ContributesAndroidInjector(modules = TasksActivityModule.class)
    abstract TasksActivity tasksActivityInjector();

    @PerApplication
    @Provides
    public static JsonAdapter<PingingTaskResult> pingingTaskResultJsonAdapter(Moshi moshi) {
        return moshi.adapter(PingingTaskResult.class);
    }

    @PerApplication
    @Provides
    public static JsonAdapter<HttpTaskResult> httpTaskResultJsonAdapter(Moshi moshi) {
        return moshi.adapter(HttpTaskResult.class);
    }

    @PerApplication
    @Provides
    public static Moshi moshi() {return new Moshi.Builder().build();}

    @Binds
    abstract Pinger pinger(HybridPinger hybridPinger);

    static HybridPinger hybridPinger() {return new HybridPinger();}

}
