package com.evartem.remsimon.data.types.pinging;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.evartem.remsimon.DI.AppComponent;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.types.base.TaskResult;
import com.evartem.remsimon.data.types.base.TaskType;
import com.google.common.base.Strings;
import com.squareup.moshi.JsonAdapter;

import org.jetbrains.annotations.NotNull;
import org.joda.time.Instant;

import java.io.IOException;

import javax.inject.Inject;

import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This task issues periodic ping requests to the provided URL
 */
@Entity(tableName = TaskType.PINGING,
        indices = @Index({"taskId"}))
public class PingingTask extends MonitoringTask {

    /**
     * The component that actually performs pinging.
     */
    @Ignore
    @Inject
    Pinger pinger;

    @Embedded
    public PingingTaskSettings settings;

    @Ignore
    @Inject
    JsonAdapter<PingingTaskResult> jsonAdapter;

    @Ignore
    public PingingTask(@NonNull String description) {
        this(description, MonitoringTask.MODE_STOPPED, "");
        settings = new PingingTaskSettings();
    }

    @Ignore
    public PingingTask(@NonNull String description, int mode) {
        this(description, mode, "");
        settings = new PingingTaskSettings();
    }

    /**
     * SHOULD NOT be used directly in the app! A special constructor for the Room library.
     */
    public PingingTask(@NonNull String description, int mode, String lastResultJson) {
        super(description, mode, lastResultJson);

        pinger = new HybridPinger();
    }

    /**
     * A convenience method for quick creation of a pinging task
     */
    public static PingingTask create(@NonNull String description, int runTaskEveryMs,
                                     @NonNull String addressToPing, int pingTimeoutMs) {
        PingingTask task = new PingingTask(description);
        task.setRunTaskEveryMs(runTaskEveryMs);
        task.settings.setPingAddress(addressToPing);
        task.settings.setPingTimeoutMs(pingTimeoutMs);
        return task;
    }

    /**
     * Injecting dependencies. Should be called right after getting the task from the Room.
     * Creates the result object from the saved in the Room JSON string.
     * @param appComponent
     */
    public void injectDependencies(AppComponent appComponent) {
        appComponent.inject(this);

        // Empty lastResultCached is created in the base class, but if we already have a JSON-serialized result in the Room DB -> unpack it
        if (!Strings.isNullOrEmpty(lastResultJson)) {
            try {
                lastResultCached = jsonAdapter.fromJson(lastResultJson);
            } catch (IOException e) {
                Timber.e(e);
                lastResultCached = new PingingTaskResult(true, 0);
            }
        } else
            lastResultCached = new PingingTaskResult(true, 0);
    }

    /**
     * Replaces the default pinger (for unit tests)
     *
     * @param pinger the class that implements {@link Pinger} and actually performs pinging
     */
    public void setPinger(@NotNull Pinger pinger) {
        this.pinger = pinger;
    }

    /**
     * Sets or replaces the current JSON adapter (for unit tests)
     * @param jsonAdapter
     */
    public void setJsonAdapter(JsonAdapter<PingingTaskResult> jsonAdapter) {
        this.jsonAdapter = jsonAdapter;
    }

    @Override
    public String getType() {
        return TaskType.PINGING;
    }


    @Override
    @WorkerThread
    protected void doTheActualWork() {

        checkNotNull(jsonAdapter);
        checkNotNull(pinger);
        checkNotNull(settings);

        PingingTaskSettings pingSettings;

        synchronized (this) {
            pingSettings = settings.clone();
        }

        PingingTaskResult result = pinger.ping(pingSettings);

        formatAndSetResult(result);

        taskGotNewResult = true;
    }

    /**
     * Processes the result of the pinging work:
     * 1. Calculates uptime and downtime
     * 2. Converts the result into JSON formatted strings
     * 3. Saves the result object for calculations on the next call
     * @param result
     */
    private void formatAndSetResult(@NotNull PingingTaskResult result) {

        result = calculateUpDownTime(result);

        String jsonResult;

        try {
            jsonResult = jsonAdapter.toJson(result);
        } catch (Exception e) {
            Timber.wtf(e);
            jsonResult = "Critical Object -> JSON conversion error";
        }

        synchronized (this) {
            lastResultJson = jsonResult;
            lastResultCached = result;
        }
    }

    /**
     * Calculates the uptime and downtime (depending on the ping results)
     * @param result The latest ping command results
     * @return The input parameter with uptime and downtime calculated
     */
    private PingingTaskResult calculateUpDownTime(PingingTaskResult result) {
        long nowMs = Instant.now().getMillis();

        if (result.pingOK) {
            result.lastSuccessTime = nowMs;
            if (lastResultCached.errorCode != TaskResult.NO_ERROR ||
                    lastResultCached.lastSuccessTime == 0) // If this is the first successful ping after creation or a failure
                result.firstSuccessTime = result.lastSuccessTime; // Memorize for uptime calculation
            else {
                result.firstSuccessTime = lastResultCached.firstSuccessTime;
                if (result.firstSuccessTime == 0) { // The task was just created
                    result.firstSuccessTime = nowMs;
                }
            }

            result.uptimeMs = nowMs - result.firstSuccessTime;
        } else {
            result.lastSuccessTime = lastResultCached.lastSuccessTime;
            result.downtimeMs = nowMs - result.lastSuccessTime;
        }
        return result;
    }

    /**
     * Returns the result of the last work.
     * Package-private - should be used for test only.
     * Clients should only request the JSON formatted result through {@link getLastResultJson}
     */
    PingingTaskResult getLastResult() {
        if (lastResultCached instanceof PingingTaskResult)
            return (PingingTaskResult) lastResultCached;
        return null;
    }
}
