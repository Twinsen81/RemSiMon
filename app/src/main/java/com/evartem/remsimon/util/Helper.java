package com.evartem.remsimon.util;

import android.content.Context;
import android.util.Pair;

import com.evartem.remsimon.R;
import com.evartem.remsimon.data.types.base.TaskType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Helper {

    private static final Map<String, Integer> uiTaskTypeNames = new HashMap<>();

    static
    {
        uiTaskTypeNames.put(TaskType.PINGING, R.string.taskType_Pinging);
        uiTaskTypeNames.put(TaskType.HTTP, R.string.taskType_Http);
    }

    /**
     *  Returns types of tasks available for creation.
     * @param context The context to access the resources
     * @return The list of pairs: internal type name - UI type name
     */
    /*public static List<Pair<String, String>> getAllTypes(Context context) {
        List<Pair<String, String>> names = new ArrayList<>();
        for (Map.Entry<String, Integer> name: uiTaskTypeNames.entrySet()) {
            names.add(new Pair(name.getKey(), context.getResources().getString(name.getValue())));
        }
        return names;
    }
*/
    public static Pair<List<String>, List<String>> getAllTypes(Context context) {
        Pair<List<String>, List<String>> names = new Pair<>(new ArrayList<>(), new ArrayList<>());
        for (Map.Entry<String, Integer> name: uiTaskTypeNames.entrySet()) {
            names.first.add(name.getKey());
            names.second.add(context.getResources().getString(name.getValue()));
        }
        return names;
    }

}
