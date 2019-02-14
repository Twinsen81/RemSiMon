package com.evartem.remsimon.data.types.http;

import android.support.test.runner.AndroidJUnit4;

import com.evartem.remsimon.TheApp;
import com.evartem.remsimon.di.AppComponent;

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

    private static final String JSON_SIMPLE_1 = "{\"Location\": \"home\", \"temperature\":24}";
    private static final String JSON_SIMPLE_2 = "{\"Location\": \"home\", \"temperature\":25}";
    private static final String JSON_SIMPLE_3 = "{\"Location\": \"home\", \"temperature\":26}";
    private static final String JSON_SIMPLE_4 = "{\"Location\": \"home\", \"temperature\":27}";
    private static final String JSON_SIMPLE_5 = "{\"Location\": \"home\", \"temperature\":28}";
    private static final String JSON_SIMPLE_6 = "{\"Location\": \"home\", \"temperature\":29}";

    private HttpTask task;

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

    @Test
    public void shouldSaveANumberOfLastResponses() {
        // Given 6 JSON responses
        RESTMockServer.whenGET(pathEndsWith("/data"))
                .thenReturnString(200, JSON_SIMPLE_1)
                .thenReturnString(200, JSON_SIMPLE_2)
                .thenReturnString(200, JSON_SIMPLE_3)
                .thenReturnString(200, JSON_SIMPLE_4)
                .thenReturnString(200, JSON_SIMPLE_5)
                .thenReturnString(200, JSON_SIMPLE_6);
        // and the setting to save last 4 responses
        task.settings.setHistoryDepth(4);
        task.settings.setFields("temperature");
        // When all the responses are received and parsed
        task.doTheWork();
        task.doTheWork();
        task.doTheWork();
        task.doTheWork();
        task.doTheWork();
        task.doTheWork();
        // Then the last 4 responses should be saved
        Map<String, List<String>> result = task.getLastResult().responses;
        assertTrue(result.containsKey("temperature"));
        assertEquals(4, result.get("temperature").size());
        assertEquals("26", result.get("temperature").get(0));
        assertEquals("27", result.get("temperature").get(1));
        assertEquals("28", result.get("temperature").get(2));
        assertEquals("29", result.get("temperature").get(3));
    }

}
