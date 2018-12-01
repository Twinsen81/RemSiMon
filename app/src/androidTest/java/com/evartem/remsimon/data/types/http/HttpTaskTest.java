package com.evartem.remsimon.data.types.http;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.evartem.remsimon.DI.RetrofitModule;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class HttpTaskTest {


    @Before
    public void init() {
    }

    @After
    public void cleanup() {

    }

    @Test
    public void createTask_SuccessfullyGetsData() throws InterruptedException {
        HttpTask task = HttpTask.create("Test task", 5000, "http://sonoff1.iglinetic.mykeenetic.com/data", "data");
        task.httpApi = RetrofitModule.generalApi(RetrofitModule.retrofit());
        task.doTheWork();
        Log.d("WORKRESULT2",  task.getLastResultJson());
    }


}
