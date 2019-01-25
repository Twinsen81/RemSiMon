package com.evartem.remsimon.data.types.base;

import java.util.Arrays;
import java.util.List;

/**
 * Defines types of tasks available in the app
 */
public final class TaskType {
    private TaskType() {}

    public static final String PINGING = "PingingTask";
    public static final int PINGING_INT = 1;

    public static final String HTTP = "HttpTask";
    public static final int HTTP_INT = 2;
}
