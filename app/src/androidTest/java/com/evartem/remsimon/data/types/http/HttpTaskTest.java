package com.evartem.remsimon.data.types.http;

import android.support.test.runner.AndroidJUnit4;

import com.evartem.remsimon.DI.AppComponent;
import com.evartem.remsimon.TheApp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.appflate.restmock.JVMFileParser;
import io.appflate.restmock.RESTMockServer;
import io.appflate.restmock.RESTMockServerStarter;
import static io.appflate.restmock.utils.RequestMatchers.pathEndsWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class HttpTaskTest {


    @Before
    public void init() {
        RESTMockServerStarter.startSync(new JVMFileParser());
    }

    @After
    public void cleanup() {

    }

    @Test
    public void createTask_SuccessfullyGetsData() throws InterruptedException {
        TheApp app = (TheApp) getInstrumentation().getTargetContext().getApplicationContext();
        AppComponent appComponent = app.getAppComponent();

        HttpTask task = HttpTask.create("Test task", 5000, "http://sonoff1.iglinetic.keenetic.pro/data", "data");
        appComponent.inject(task);
        //task.httpApi = appComponent.generalApi();
        task.doTheWork();

        HttpTask task2 = HttpTask.create("Test task2", 5000, RESTMockServer.getUrl() + "data", "data");
        appComponent.inject(task2);

        RESTMockServer.whenGET(pathEndsWith("/data")).thenReturnString(200, "{user: 100}");

        //task2.httpApi = appComponent.generalApi();
        task2.doTheWork();

    }


}
