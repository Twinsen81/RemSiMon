package com.evartem.remsimon.DI;

import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class AppContextModule {

    Application app;
    public AppContextModule(Application application) {
        this.app = application;
    }

    @Provides
    Application getContext() {
        return app;
    }

}
