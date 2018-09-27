package com.evartem.remsimon.data.types.base;


import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.google.common.base.Strings;
import com.squareup.moshi.Moshi;

import org.joda.time.Duration;
import org.joda.time.Instant;

import java.util.UUID;

import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * A common interface that each task of the app must implement
 */
public abstract class MonitoringTask {

    @PrimaryKey
    @NonNull
    protected String taskId;


    private String description;

    /**
     * The value returned by last call to doTheActualWork.
     * JSON formatted string.
     */
    @NonNull
    protected String lastResultJson;

    @Ignore
    protected static Moshi moshi = new Moshi.Builder().build();

    /**
     * The last result of the doTheActualWork routine is saved here in addition to the lastResultJson field
     * to provide fast access to the lastSuccessTime field which, in case of the task's failure, must
     * be transferred from the previous result to the current.
     */
    @Ignore
    @NonNull
    protected TaskResult lastResultCached;

    /**
     * The doTheActualWork method of this task will be called every "runTaskEveryNSeconds" seconds
     */
    private int runTaskEveryNSeconds = 5;

    @Ignore
    protected volatile boolean stateChanged;

    //protected volatile long lastSuccessTime;

    public static final int MODE_STOPPED = 0;
    public static final int MODE_ACTIVE = 1;
    public static final int MODE_DEACTIVATED = 2;

    private volatile int mode;



    private enum WorkStage {STOPPED, INPROGRESS, FINISHED}

    @Ignore
    WorkStage workStage;


    @Ignore
    Instant lastTimeDidWork; // When this task last time did the work


    public MonitoringTask(@NonNull String description, int mode, String lastResultJson) {
        this.taskId = UUID.randomUUID().toString();
        this.description = description;
        this.mode = mode;
        this.lastResultJson = lastResultJson;
        lastResultCached = new TaskResult();
        workStage = WorkStage.STOPPED;
        lastTimeDidWork = Instant.now().minus(Duration.standardDays(1));
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


    public synchronized int getMode() {
        return mode;
    }

    /**
     * Returns a non-empty string when an important notification should be shown to the user
     *
     * @return An important notification to show to the user, if no notification available at the moment - empty string
     */
    public synchronized void setDescription(@NonNull String description) {
        if (Strings.isNullOrEmpty(description)) {
            this.description = "New " + getType();
        } else {
            this.description = description;
        }
    }

    public synchronized String getDescription() {
        return description;
    }

    public synchronized int getRunTaskEveryNSeconds() {
        return runTaskEveryNSeconds;
    }

    public synchronized void setRunTaskEveryNSeconds(int runTaskEveryNSeconds) {
        this.runTaskEveryNSeconds = runTaskEveryNSeconds;
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


    public synchronized boolean isTimeToExecute() {
        return Instant.now().minus(lastTimeDidWork.getMillis()).getMillis() > (runTaskEveryNSeconds * 1000);
    }


    /**
     * Executes the task's work.
     * Works synchronously, so the call is blocking until the work is done, e.g.
     * until the ping request is returned or timed out.
     * Therefore this method must be executed on a non-UI thread.
     */
    @WorkerThread
    public void doTheWork() {
        if (isWorking()) return;

        stateChanged = false;
        signalWorkStarted();
        try {
            doTheActualWork();
        } catch (Exception e) {
            Timber.wtf(e);
        }
        signalWorkFinished();
    }

    private synchronized void signalWorkStarted() {
            workStage = WorkStage.INPROGRESS;
    }

    private synchronized void signalWorkFinished() {
            workStage = WorkStage.FINISHED;
            lastTimeDidWork = Instant.now();
    }

    public synchronized boolean isWorking() {
            return workStage == WorkStage.INPROGRESS;
    }
    public synchronized boolean isFinshed() {
            return workStage == WorkStage.FINISHED;
    }

    /**
     * The method that is actually doing the task's work in an implementation class.
     */
    protected abstract void doTheActualWork();

    /**
     * Returns current state (e.g. data received) of the monitoring task as text string OR JSON string.
     *
     * @return The tasks received data or a description of the current status of the task, e.g.
     * "PING 127.0.0.1 is OK"
     * "The temperature is 27 C"
     *  Some data represented as a JSON string
     */
    @NonNull
    public synchronized String getLastResultJson() {
        return lastResultJson;
    }

    /**
     * @return The type of this task as defined in {@link TaskType}
     */
    public abstract String getType();


    @NonNull
    public String getTaskId() {return taskId;}

    /**
     * Should NOT be used directly! Inserted only for the Room library's sake
     * @param taskId
     */
    public void setTaskId(@NonNull String taskId) {this.taskId = taskId;}

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MonitoringTask && ((MonitoringTask) obj).getTaskId().equals(taskId);
    }

    public synchronized void copyPropertiesFrom(MonitoringTask sourceTask) {
        description = sourceTask.description;
        lastResultCached = sourceTask.lastResultCached;
        lastResultJson = sourceTask.lastResultJson;
        lastTimeDidWork = sourceTask.lastTimeDidWork;
        mode = sourceTask.mode;
        runTaskEveryNSeconds = sourceTask.runTaskEveryNSeconds;
        workStage = sourceTask.workStage;
    }
}
