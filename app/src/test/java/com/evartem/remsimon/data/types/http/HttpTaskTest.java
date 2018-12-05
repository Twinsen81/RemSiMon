package com.evartem.remsimon.data.types.http;

import com.evartem.remsimon.DI.RetrofitModule;
import com.evartem.remsimon.data.util.StandardOutputLoggingTree;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
//        HttpTask task2 = HttpTask.create("Test task2", 5000, RESTMockServer.getUrl() + "data", "data");
//        task2.httpApi = RetrofitModule.generalApi(RetrofitModule.retrofit(RetrofitModule.okHttpClient()));
//        RESTMockServer.whenGET(pathEndsWith("/data")).thenReturnString(200, "{user: 101}");
//        task2.doTheWork();

        String json1 = "{ \"child\": { \"something\": \"value\", \"something2\": \"value\" } }";
        String json2 = "{\"user\": \"101\"}";
        String str = "{\"message\":\"Hi\",\"place\":{\"name\":\"World!\"}}";
        String str2 = "{\"Cars\":[{\"name\":\"Audi\",\"model\":\"2012\",\"price\":22000,\"colours\":[\"gray\",\"red\",\"white\"]},\n" +
                " {\"name\":\"Skoda\",\"model\":\"2009\",\"price\":14000,\"colours\":[\"black\",\"gray\",\"white\"]},\n" +
                " {\"name\":\"Volvo\",\"model\":\"2010\",\"price\":19500,\"colours\":[\"black\",\"silver\",\"beige\"]}]}";
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

        readJson(json1);
        System.out.println("------------");
        readJson(json2);
        System.out.println("------------");
        readJson(str);
        System.out.println("------------");
        readJson(str2);


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

    private static void readJson(String json) throws IOException {
        InputStream in = new ByteArrayInputStream(json.getBytes(Charset.forName("UTF-8")));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        JsonParser parser = new JsonParser();
        JsonElement tree = parser.parse(reader);

        goThroughJson("", tree);
    }

    private static void goThroughJson(String keyName, JsonElement el) {
        if (el.isJsonPrimitive())
            System.out.println(keyName + " = " + el.getAsJsonPrimitive().toString());
        if (el.isJsonArray()) {
            el.getAsJsonArray().forEach(arrayElement -> goThroughJson(keyName, arrayElement));
        } else if (el.isJsonObject()) {
            el.getAsJsonObject().keySet().forEach(key -> goThroughJson(keyName + "." + key, el.getAsJsonObject().get(key)));
        }
    }
}
