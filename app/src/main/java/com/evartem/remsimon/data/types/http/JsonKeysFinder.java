package com.evartem.remsimon.data.types.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The class parses through JSON data looking for specified keys (case insensitive) and
 * saving their values as a (key,value) map.
 */
public class JsonKeysFinder {

    private Set<String> keys2Find = new HashSet<>();
    private Map<String, String> result = new HashMap<>();

    /**
     * @param keys2Find comma separated names (case insensitive)
     */
    public JsonKeysFinder(String keys2Find) {
        if (keys2Find != null && keys2Find.trim().length() > 0)
            populateKeysSet(keys2Find.trim());
    }

    /**
     * Split string by "," and add the slitted values into a Set
     *
     * @param keys2Find comma separated names (case insensitive)
     */
    private void populateKeysSet(String keys2Find) {
        String[] keys = keys2Find.toLowerCase().split(",");
        for (String key : keys) {
            if (key.trim().length() > 0)
                this.keys2Find.add(key.trim());
        }
    }

    public Map<String, String> getKeysAndValues(String jsonData) {
        if (jsonData == null || jsonData.trim().length() == 0)
            return result;

        try {
            findKeyValuesInJson(jsonData);
        } catch (Exception ignored) {
        }
        return result;
    }

    /**
     * Goes through all keys of the JSON data looking for the keys needed (keys2Find).
     * Once found, puts that key's value into the map (result) and removes it from the kays2Find set.
     * If there're multiple keys with the same name - only the first found is accepted.
     *
     * @param json - JSON data as a string to look in for the keys2Find
     * @throws IOException
     */
    private void findKeyValuesInJson(String json) throws IOException {
        InputStream in = new ByteArrayInputStream(json.getBytes(Charset.forName("UTF-8")));
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        JsonParser parser = new JsonParser();
        JsonElement tree = parser.parse(reader);
        iterateAndFindKeyValue("", tree);
    }

    private void iterateAndFindKeyValue(String currElementName, JsonElement element) {
        if (element.isJsonPrimitive() && keys2Find.contains(currElementName)) {
            keys2Find.remove(currElementName);
            result.put(currElementName, element.getAsJsonPrimitive().toString().replaceAll("^\"|\"$", "")); // Removing double quotes (if have any)
            return;
        }
        if (element.isJsonArray()) {
            for (JsonElement arrayElement : element.getAsJsonArray()) {
                iterateAndFindKeyValue(currElementName, arrayElement);
                if (keys2Find.isEmpty()) return; // All keys have been found
            }
        } else if (element.isJsonObject()) {
            for (String key : element.getAsJsonObject().keySet()) {
                iterateAndFindKeyValue(currElementName.isEmpty() ? key.toLowerCase() : currElementName + "." + key.toLowerCase(), element.getAsJsonObject().get(key));
                if (keys2Find.isEmpty()) return; // All keys have been found
            }
        }
    }
}
