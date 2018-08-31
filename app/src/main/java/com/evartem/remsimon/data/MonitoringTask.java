package com.evartem.remsimon.data;


import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.evartem.remsimon.data.types.TaskType;
import com.google.common.base.Strings;

import org.joda.time.Instant;

//TODO Add moshi and return result as JSON
//TODO Add to MinitoringTask field: String lastResult (getters and setters sync)
//TODO Add superclass TaskResult with String lastSuccessTime, String error
//TODO Add PingingTaskResult extends TaskResult
//TODO remove lastSuccessTime



/**
 * A common interface that each task of the app must implement
 */
public abstract class MonitoringTask {

    @PrimaryKey
    @NonNull
    protected final String taskEntryId;


    private String description;

    /**
     * The doTheWork method of this task will be called every "everyNSeconds" seconds
     */
    private int everyNSeconds = 5;

    @Ignore
    protected volatile boolean stateChanged;

    protected volatile long lastSuccessTime;

    public static final int MODE_STOPPED = 0;
    public static final int MODE_ACTIVE = 1;
    public static final int MODE_DEACTIVATED = 2;
    public static final int MODE_SHUTDOWN = 3;

    private volatile int mode;


    public MonitoringTask(@NonNull String taskEntryId, @NonNull String description, int mode, long lastSuccessTime) {
        this.taskEntryId = taskEntryId;
        this.description = description;
        this.mode = mode;
        this.lastSuccessTime = lastSuccessTime;
    }

    /**
     * Makes the task active - it's actively doing the work
     */
    public synchronized void activate() {
        mode = MonitoringTask.MODE_ACTIVE;
    }

    /**
     * The work is paused, the task is idling until the user manually activates it through activate()
     */
    public synchronized void deactivate() {
        mode = MonitoringTask.MODE_DEACTIVATED;
    }

    /**
     * The task is no longer needed, it will be deleted
     */
    public void shutdown() {
        mode = MonitoringTask.MODE_SHUTDOWN;
    }

    public synchronized int getMode() {
        return mode;
    }


    /**
     * Returns a non-empty string when an important notification should be shown to the user
     *
     * @return An important notification to show to the user, if no notification available at the moment - empty string
     */
    public void setDescription(@NonNull String description) {
        if (Strings.isNullOrEmpty(description)) {
            this.description = "New " + getType();
        } else {
            this.description = description;
        }
    }

    public String getDescription() {
        return description;
    }

    public synchronized int getEveryNSeconds() {
        return everyNSeconds;
    }

    public synchronized void setEveryNSeconds(int everyNSeconds) {
        this.everyNSeconds = everyNSeconds;
    }

    public String getNotification() {
        return "";
    }


    /**
     * Checks if the task's state has changed and thus, UI must be updated.
     * The "changed" flag is cleared in this method. So the second consecutive call will
     * most likely return false.
     *
     * @return true if the task's state change
     */
    public boolean getStateChange() {
        return stateChanged;
    }

    /**
     * The method that is actually doing the task's work.
     * Works synchronously, so the call is blocking until the work is done, e.g.
     * until the ping request is returned or timed out.
     * Therefore this method must be executed on a non-UI thread.
     */
    @WorkerThread
    public abstract void doTheWork();

    /**
     * Returns current state (e.g. data received) of the monitoring task as text string OR JSON string.
     *
     * @return The tasks received data or a description of the current status of the task, e.g.
     * "PING 127.0.0.1 is OK"
     * "The temperature is 27 C"
     *  Some data represented as a JSON string
     */
    public synchronized String getStateText() {
        return (mode == MODE_ACTIVE ? "Active: " : "Not active: ") + description;
    }


    /**
     * @return The type of this task as defined in {@link TaskType}
     */
    public abstract String getType();


    public String getTaskEntryId() {return taskEntryId;}

    /**
     * @return The moment when the task successfully completed it's job the last time (e.g. a successful ping of a URL)
     */
    public synchronized Instant getLastSuccessInstant() {
        return  new Instant(lastSuccessTime);
    }

    public synchronized long getLastSuccessTime() {
        return lastSuccessTime;
    }




    /**
     * Copy settings from another task (copying a task without creating a new instance)
     * @param source The task to copy the settings from
     */
    //void copySettings(@NonNull MonitoringTask source);
}
