package com.evartem.remsimon.data.types.base;


import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.google.common.base.Strings;

import org.joda.time.Duration;
import org.joda.time.Instant;

import java.util.UUID;

import timber.log.Timber;


/**
 * The common functionality that each task of the app must have.
 * The implemented public methods are threadsafe via the "synchronized" keyword
 */
public abstract class MonitoringTask {

    @PrimaryKey
    @NonNull
    protected String taskId;

    /**
     * The task's description that appears in the UI
     */
    private String description;

    /**
     * The value returned by last call to doTheActualWork.
     * JSON formatted string.
     */
    @NonNull
    protected String lastResultJson;

    /**
     * The last result of the doTheActualWork routine is saved here in addition to the lastResultJson field
     * to provide fast access to the lastSuccessTime field which, in case of the task's failure, must
     * be transferred from the previous result to the current.
     */
    @Ignore
    @NonNull
    protected TaskResult lastResultCached;

    /**
     * The doTheActualWork method of this task will be called every "runTaskEveryMs" milliseconds
     */
    private int runTaskEveryMs = 5000;

    /**
     * Notifies the tasks manager that this task has completed and a new result is available for displaying on the UI
     */
    @Ignore
    protected volatile boolean taskGotNewResult = false;

    /**
     * The task can be in 3 modes:
     * STOPPED - was created but has never been started yet
     * ACTIVE - the task is executed periodically by the task manager according to the task's runTaskEveryMs
     * DEACTIVATED - the task has been deactivated by the user and is not executed periodically by the task manager
     */
    private volatile int mode;
    public static final int MODE_STOPPED = 0;
    public static final int MODE_ACTIVE = 1;
    public static final int MODE_DEACTIVATED = 2;


    /**
     * Indicates the stage of the task manager's processing of this task:
     * STOPPED - the task was created but it hasn't been executed by the manager even once
     * IN_PROGRESS - the task is being executed by the manager now
     * FINISHED_AWAITING_NEXT_EXECUTION - the task was executed by the manager and now awaits the next execution according to the task's runTaskEveryMs
     */
    @Ignore
    private WorkStage workStage;
    private enum WorkStage {STOPPED, IN_PROGRESS, FINISHED_AWAITING_NEXT_EXECUTION}


    /**
     * Indicated the last time the execution of this task was completed.
     * Is used to calculate the next execution time according to the task's runTaskEveryMs
     */
    @Ignore
    private Instant lastTimeDidWork;


    public MonitoringTask(@NonNull String description, int mode, @NonNull String lastResultJson) {
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

    public synchronized int getRunTaskEveryMs() {
        return runTaskEveryMs;
    }

    public synchronized void setRunTaskEveryMs(int runTaskEveryMs) {
        this.runTaskEveryMs = runTaskEveryMs;
    }

    /**
     * Checks if the task has got new result and thus, UI must be updated.
     * The taskGotNewResult flag is cleared in this method. So the second consecutive call will
     * most likely return false.
     *
     * @return true if the task has an updated result that must be shown to the user
     */
    public boolean gotNewResult() {
        boolean newResult = taskGotNewResult;
        taskGotNewResult = false;
        return newResult;
    }


    /**
     * Checks if the time has come to execute the task again
     * @return true - execute the task now, false - the right time hasn't come yet
     */
    public synchronized boolean isTimeToExecute() {
        return Instant.now().minus(lastTimeDidWork.getMillis()).getMillis() > runTaskEveryMs;
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

        signalWorkStarted();
        try {
            doTheActualWork();
        } catch (Exception e) {
            Timber.wtf(e);
        }
        signalWorkFinished();
    }

    private synchronized void signalWorkStarted() {
            workStage = WorkStage.IN_PROGRESS;
    }

    private synchronized void signalWorkFinished() {
            workStage = WorkStage.FINISHED_AWAITING_NEXT_EXECUTION;
            lastTimeDidWork = Instant.now();
    }

    public synchronized boolean isWorking() {
            return workStage == WorkStage.IN_PROGRESS;
    }
    public synchronized boolean isFinished() {
            return workStage == WorkStage.FINISHED_AWAITING_NEXT_EXECUTION;
    }

    /**
     * The method that is actually doing the task's work in an implementation class.
     */
    protected abstract void doTheActualWork();

    /**
     * Returns current state (e.g. data received) of the monitoring task as text (JSON) string.
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

/*    public synchronized void copyPropertiesFrom(MonitoringTask sourceTask) {
        description = sourceTask.description;
        lastResultCached = sourceTask.lastResultCached;
        lastResultJson = sourceTask.lastResultJson;
        lastTimeDidWork = sourceTask.lastTimeDidWork;
        mode = sourceTask.mode;
        runTaskEveryMs = sourceTask.runTaskEveryMs;
        workStage = sourceTask.workStage;
    }*/
}
