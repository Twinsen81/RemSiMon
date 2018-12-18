package com.evartem.remsimon.data.types.pinging;

import com.evartem.remsimon.data.types.base.TaskResult;

public class PingingTaskResult extends TaskResult {
    public boolean pingOK = false; // Last ping successful?
    public long pingTimeMs = 0; // Last ping time in milliseconds
    public long uptimeMs = 0; // The uptime of the server being pinged (the duration from the first successful ping until now)
    public long downtimeMs = 0; // The downtime of the server being pinged (the duration from the first failed ping until now)

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

    /*public PingingTaskResult clone() {
        PingingTaskResult clone = (PingingTaskResult)super.clone(new PingingTaskResult(pingOK, pingTimeMs));
        clone.uptimeMs = uptimeMs;
        clone.downtimeMs = downtimeMs;
        return clone;
    }*/
}
