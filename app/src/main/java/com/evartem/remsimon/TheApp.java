package com.evartem.remsimon;

import android.app.Application;
import com.squareup.leakcanary.LeakCanary;
import net.danlew.android.joda.JodaTimeAndroid;

public class TheApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        JodaTimeAndroid.init(this);
    }
}
