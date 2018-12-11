package com.evartem.remsimon.data.types.http;

import android.support.test.runner.AndroidJUnit4;

import com.evartem.remsimon.DI.AppComponent;
import com.evartem.remsimon.TheApp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

import io.appflate.restmock.JVMFileParser;
import io.appflate.restmock.RESTMockServer;
import io.appflate.restmock.RESTMockServerStarter;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static io.appflate.restmock.utils.RequestMatchers.pathEndsWith;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class HttpTaskTest {

    private static final String JSON_SIMPLE_NUMBER = "{\"name\": \"Evgeniy\", \"age\":37}";
    HttpTask task;

    @Before
    public void init() {

        RESTMockServerStarter.startSync(new JVMFileParser());
        TheApp app = (TheApp) getInstrumentation().getTargetContext().getApplicationContext();
        AppComponent appComponent = app.getAppComponent();

        task = HttpTask.create("Test task", 5000, RESTMockServer.getUrl() + "data", "name,age");
        appComponent.inject(task);
    }

    @Test
    public void shouldReceiveDataAndSuccessfullyParseIt() {
        // Given a successfully received JSON data
        RESTMockServer.whenGET(pathEndsWith("/data")).thenReturnString(200, JSON_SIMPLE_NUMBER);
        // When the data is received and parsed
        task.doTheWork();

        // Then the task's result (response) should contain 2 exact values
        Map<String, List<String>> result = task.getLastResult().responses;
        assertEquals(2, result.size());
        assertTrue(result.containsKey("name"));
        assertEquals(1, result.get("name").size());
        assertEquals("Evgeniy", result.get("name").get(0));
        assertTrue(result.containsKey("age"));
        assertEquals(1, result.get("age").size());
        assertEquals("37", result.get("age").get(0));
    }

    @Test
    public void shouldReturnEmptyResponseOnServerError() {
        // Given an error from the server
        RESTMockServer.whenGET(pathEndsWith("/data")).thenReturnString(500, "");
        // After the data is received and parsed
        task.doTheWork();

        // Then the task's result should contain nothing
        Map<String, List<String>> result = task.getLastResult().responses;
        assertEquals(0, result.size());
    }

    // Request times out + Retrofit - set timeout
    // Saving responses in a list
}
