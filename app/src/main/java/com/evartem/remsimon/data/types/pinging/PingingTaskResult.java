package com.evartem.remsimon.data.types.pinging;

import com.evartem.remsimon.data.types.base.TaskResult;

public class PingingTaskResult extends TaskResult {
    public boolean lastPingOK = false; // Last ping successful?
    public long pingTimeMs = 0; // Last ping time in milliseconds
    public long uptimeMs = 0; // The uptime of the server being pinged (the duration from the first successful ping until now)
    public long downtimeMs = 0; // The downtime of the server being pinged (the duration from the first failed ping until now)
    public long failedPingsCount = 0; // The number of times the ping command consecutively failed
    public long downtimeEndedTime = 0; // The time when the last downtime had ended and the uptime has begun
    public long lastDowntimeDurationMs = 0; // The duration of the last downtime

    public PingingTaskResult(boolean lastPingOK, long pingTimeMs) {
        this.lastPingOK = lastPingOK;
        this.pingTimeMs = pingTimeMs;
    }

    public PingingTaskResult(boolean lastPingOK, long pingTimeMs, int errorCode, String errorMessage) {
        this.lastPingOK = lastPingOK;
        this.pingTimeMs = pingTimeMs;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public PingingTaskResult(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * Unlike lastPingOK this method takes into account the attempts setting (downtimeFailedPingsNumber).
     * It returns false only when the ping command failed more than {PingingTaskSettings.downtimeFailedPingsNumber} times
     * @return Ping failed after several attempts
     */
    public boolean isPingOK() {
        return downtimeMs == 0;
    }

    @Override
    protected TaskResult clone(TaskResult copy) {
        super.clone(copy);
        if (copy instanceof PingingTaskResult) {
            PingingTaskResult copyPingingTaskResult = (PingingTaskResult)copy;
            copyPingingTaskResult.lastPingOK = lastPingOK;
            copyPingingTaskResult.pingTimeMs = pingTimeMs;
            copyPingingTaskResult.uptimeMs = uptimeMs;
            copyPingingTaskResult.downtimeMs = downtimeMs;
            copyPingingTaskResult.failedPingsCount = failedPingsCount;
            copyPingingTaskResult.downtimeEndedTime = downtimeEndedTime;
            copyPingingTaskResult.lastDowntimeDurationMs = lastDowntimeDurationMs;
        }
        return copy;
    }

    public PingingTaskResult clone() {
        PingingTaskResult copy = new PingingTaskResult(errorCode, errorMessage);
        return (PingingTaskResult)clone(copy);
    }
}
