package com.evartem.remsimon.data;


import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

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
     * Makes the task active - it's actively doing the work
     */
    void activate();

    /**
     * The work is paused, the task is idling until the user manually activates it through activate()
     */
    void deactivate();

    /**
     * The task is no longer needed, it will be deleted
     */
    void shutdown();

    public static final int STATE_STOPPED = 0;
    public static final int STATE_ACTIVE = 1;
    public static final int STATE_DEACTIVATED = 2;

    int getState();

    /**
     * Checks if the task's state has changed and thus, UI must be updated.
     * The "changed" flag is cleared in this method. So the second consecutive call will
     * most likely return false.
     * @return true if the task's state change
     */
    boolean isStateChange();

    /**
     * The method that is actually doing the task's work.
     * Works synchronously, so the call is blocking until the work is done, e.g.
     * until the ping request is returned or timed out.
     * Therefore this method must be executed on a non-UI thread.
     */
    @WorkerThread
    void doTheWork();

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
     * Copy settings from another task (copying a task without creating a new instance)
     * @param source The task to copy the settings from
     */
    //void copySettings(@NonNull MonitoringTask source);
}
