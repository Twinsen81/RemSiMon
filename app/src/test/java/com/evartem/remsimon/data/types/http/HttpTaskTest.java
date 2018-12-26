package com.evartem.remsimon.data.types.http;

import com.evartem.remsimon.DI.AppModule;
import com.evartem.remsimon.DI.RetrofitModule;
import com.evartem.remsimon.data.util.StandardOutputLoggingTree;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import io.appflate.restmock.JVMFileParser;
import io.appflate.restmock.RESTMockServer;
import io.appflate.restmock.RESTMockServerStarter;
import timber.log.Timber;

import static io.appflate.restmock.utils.RequestMatchers.pathEndsWith;

public class HttpTaskTest {

    @Before
    public void setUp() {
        Timber.plant(new StandardOutputLoggingTree());
        RESTMockServerStarter.startSync(new JVMFileParser());
    }

    @Test
    public void shouldSuccessfullyReturnData() throws JSONException, IOException {
        RESTMockServer.whenGET(pathEndsWith("/data"))
                .thenReturnString(200, "{\"user\": \"101\", \"temp\":\"29.5\"}")
                .thenReturnString(200, "{\"user\": \"102\"}")
                .thenReturnString(200, "{\"user\": \"103\", \"temp\":\"29.7\"}")
                .thenReturnString(200, "{\"user\": \"104\"}")
                .thenReturnString(200, "{user: 105}")
                .thenReturnString(200, "{\"user\": \"106\", \"temp\":\"29.9\"}");

        HttpTask task2 = HttpTask.create("Test task2", 5000, RESTMockServer.getUrl() + "data", "data");
        task2.httpApi = RetrofitModule.generalApi(RetrofitModule.retrofit(RetrofitModule.okHttpClient()));
        task2.jsonAdapter = AppModule.httpTaskResultJsonAdapter(AppModule.moshi());
        task2.settings.setFields("user, temp");
        task2.settings.setHistoryDepth(4);
        task2.doTheWork();
        task2.doTheWork();
        task2.doTheWork();
        task2.doTheWork();
        task2.doTheWork();
        task2.doTheWork();

        System.out.println(task2.getLastResultJson());

/*
        String json1 = "{ \"child\": { \"something\": \"value\", \"something2\": \"value\" } }";
        String json2 = "{\"user\": \"101\"}";
        String str = "{\"message\":\"Hi\",\"place\":{\"name\":\"World!\"}}";
        String str2 = "{\"Cars\":[{\"name\":\"Audi\",\"model\":\"2012\",\"price\":22000,\"colours\":[\"gray\",\"red\",\"white\"]},\n" +
                " {\"name\":\"Skoda\",\"model\":\"2009\",\"price\":14000,\"colours\":[\"black\",\"gray\",\"white\"]},\n" +
                " {\"name\":\"Volvo\",\"model\":\"2010\",\"price\":19500,\"colours\":[\"black\",\"silver\",\"beige\"]}]}";*/
/*        JSONObject resobj = new JSONObject(json1);
        Iterator<?> keys = resobj.keys();
        while(keys.hasNext() ) {

            String key = (String)keys.next();
            Timber.d(key);
        }*/

/*

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(json1);
        JsonObject obj = element.getAsJsonObject(); //since you know it's a JsonObject
        Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();//will return members of your object
        for (Map.Entry<String, JsonElement> entry : entries) {
            System.out.println(entry.getKey());
        }
*/


    }

/*    private static void readJson(String json) throws IOException {
        InputStream in = new ByteArrayInputStream(json.getBytes(Charset.forName("UTF-8")));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        while (reader.hasNext()) {
            JsonToken jsonToken = reader.peek();
            if(jsonToken == JsonToken.BEGIN_OBJECT) {
                reader.beginObject();
            } else if(jsonToken == JsonToken.END_OBJECT) {
                reader.endObject();
            } if(jsonToken == JsonToken.STRING) {
                System.out.println(" = " + reader.nextString()); // print Hi World!
            } else {
                reader.skipValue();
            }
        }
        reader.close();
    }*/


}
