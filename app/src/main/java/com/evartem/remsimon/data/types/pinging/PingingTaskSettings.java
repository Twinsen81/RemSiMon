package com.evartem.remsimon.data.types.pinging;

/**
 * Defines various settings for the pinging task type.
 * The getters and setters are synchronized since the UI will
 * write settings from the UI thread, but the task will read them from
 * the worker thread
 */
public class PingingTaskSettings {
    private String pingAddress = "127.0.0.1";
    private int pingTimeoutMs = 1000;
    private int downtimeFailedPingsNumber = 2; // If ping command fails consecutively for this number of times -> start counting downtime

    public int getDowntimeFailedPingsNumber() {
        return downtimeFailedPingsNumber;
    }

    public void setDowntimeFailedPingsNumber(int downtimeFailedPingsNumber) {
        this.downtimeFailedPingsNumber = downtimeFailedPingsNumber;
    }

    public synchronized String getPingAddress() {
        return pingAddress;
    }

    public synchronized void setPingAddress(String pingAddress) {
        this.pingAddress = pingAddress;
    }

    public synchronized int getPingTimeoutMs() {
        return pingTimeoutMs;
    }

    public synchronized void setPingTimeoutMs(int pingTimeoutMs) {
        this.pingTimeoutMs = pingTimeoutMs;
    }

    public synchronized PingingTaskSettings clone() {
        PingingTaskSettings clone = new PingingTaskSettings();
        clone.setPingAddress(getPingAddress());
        clone.setPingTimeoutMs(getPingTimeoutMs());
        clone.setDowntimeFailedPingsNumber(downtimeFailedPingsNumber);
        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PingingTaskSettings &&
                pingAddress.equals(((PingingTaskSettings)obj).pingAddress) &&
                pingTimeoutMs == ((PingingTaskSettings)obj).pingTimeoutMs;
    }
}
