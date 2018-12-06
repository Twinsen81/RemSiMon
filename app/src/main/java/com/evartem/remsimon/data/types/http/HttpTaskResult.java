package com.evartem.remsimon.data.types.http;

import com.evartem.remsimon.data.types.base.TaskResult;
import com.google.common.collect.EvictingQueue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import timber.log.Timber;

public class HttpTaskResult extends TaskResult {
    public boolean responseOK = false; // Last request was successful?

    /**
     * Contains values of fields (keys) extracted from the JSON responses.
     * The historyDepth parameter in HttpTaskSettings defines how many values to keep from last responses
     * If the historyDepth is 3 and the last 5 JSON responses were:
     * {"temp":"36.5"}
     * {"temp":"37.5"}
     * {"temp":"38.3"}
     * {"temp":"39.6"}
     * {"temp":"40.1"}
     * Then values from 3 last responses will be kept: responses["temp"] = {38.3, 39.6, 40.1}
     */
    public Map<String, List<String>> responses = new HashMap<>();

    /**
     * Adds new values received from the last JSON response.
     * If the size of the list is grater than the historyDepth, then the oldest values are removed from the list
     */
    public void addResponse(Map<String, String> keysValues, int historyDepth)
    {
        for (Map.Entry<String, String> keyValue : keysValues.entrySet()) {
            List<String> values;
            if (responses.containsKey(keyValue.getKey()))
                values = responses.get(keyValue.getKey());
            else {
                values = new ArrayList<>();
                responses.put(keyValue.getKey(), values);
            }

            values.add(keyValue.getValue());

            // Remove older values (older than historyDepth)
            // while - because user might change historyDepth from, say, 5 to 3
            while (values.size() > 1 && values.size() > historyDepth)
                values.remove(0);
        }
    }
}
