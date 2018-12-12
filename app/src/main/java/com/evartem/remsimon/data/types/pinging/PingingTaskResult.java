package com.evartem.remsimon.data.types.pinging;

import com.evartem.remsimon.data.types.base.TaskResult;

public class PingingTaskResult extends TaskResult {
    public boolean pingOK = false; // Last ping successful?
    public long pingTimeMs = 0; // Last ping time in milliseconds

    public PingingTaskResult(boolean pingOK, long pingTimeMs) {
        this.pingOK = pingOK;
        this.pingTimeMs = pingTimeMs;
    }

    public PingingTaskResult(boolean pingOK, long pingTimeMs, int errorCode, String errorMessage) {
        this.pingOK = pingOK;
        this.pingTimeMs = pingTimeMs;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public PingingTaskResult(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
