package com.evartem.remsimon.data;

import java.util.Arrays;
import java.util.List;

/**
 * Defines tasks available in the app
 */
public final class TaskType {
    private TaskType() {}

    public static final String PINGING = "PingingTask";

    public static List<String> getAllTypes() {
        return Arrays.asList(PINGING);
    }
}
