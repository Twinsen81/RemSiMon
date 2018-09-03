package com.evartem.remsimon.data.types.pinging;

import com.evartem.remsimon.data.types.base.TaskResult;

public class PingingTaskResult extends TaskResult {
    public boolean pingOK = false; // Last ping successful?
    public long pingTimeMs = 0; // Last ping time in milliseconds

    public static final int ERROR_INVALID_ADDRESS = 1;
    public static final int ERROR_TIMEOUT = 2;
}
