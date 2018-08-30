package com.evartem.remsimon.data;


import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.Instant;

/**
 * A common interface that each task of the app must implement
 */
public interface MonitoringTask {

    String getDescription();

    void setDescription(String description);

    /**
     * Returns a non-empty string when an important notification should be shown to the user
     *
     * @return An important notification to show to the user, if no notification available at the moment - empty string
     */
    default String getNotification() {
        return "";
    }

    /**
     * Makes the task start issuing network requests on a separate worker thread
     */
    void activate();

    /**
     * The worker thread is stopped, no requests are being issued
     */
    void deactivate();


    public static final int STATE_STOPPED = 0;
    public static final int STATE_ACTIVE = 1;
    public static final int STATE_DEACTIVATED = 2;

    int getState();

    /**
     * Returns current state of the monitoring task as text string
     *
     * @return A short description of the current status of the task, e.g. "PING is OK"
     */
    String getStateText();

    /**
     * @return The type of this task as defined in {@link TaskType}
     */
    String getType();


    String getTaskEntryId();

    /**
     * @return The moment when the task successfully completed it's job the last time (e.g. a successful ping of a URL)
     */
    Instant getLastSuccessInstant();

    /**
     * The app is being closed - stop the task (interrupt the worker thread)
     */
    void shutdown();
}
