package com.evartem.remsimon.DI;

import android.app.Application;
import android.content.Context;

import com.evartem.remsimon.DI.Scopes.PerApplication;

import dagger.Module;
import dagger.Provides;

@Module
public class AppContextModule {

    Application app;
    public AppContextModule(Application application) {
        this.app = application;
    }

    @PerApplication
    @Provides
    Application getContext() {
        return app;
    }

}
