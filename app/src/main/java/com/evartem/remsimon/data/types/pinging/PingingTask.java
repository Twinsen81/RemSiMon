package com.evartem.remsimon.data.types.pinging;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.types.base.TaskResult;
import com.evartem.remsimon.data.types.base.TaskType;
import com.evartem.remsimon.data.types.http.HttpTaskResult;
import com.google.common.base.Strings;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.jetbrains.annotations.NotNull;
import org.joda.time.Instant;

import java.io.IOException;

import javax.inject.Inject;

import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * This task issues periodic ping requests to the provided URL
 */
@Entity(tableName = TaskType.PINGING,
        indices = @Index({"taskId"}))
public class PingingTask extends MonitoringTask {

    @Ignore
    Pinger pinger;

    /**
     * Creates the pinging task.
     *
     * @param description A short description of the task
     */
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
        //jsonAdapter = moshi.adapter(PingingTaskResult.class);

        // Empty lastResultCached is created in the base class, but if we have already a JSON-serialized result in the Room -> unpack it
        if (!Strings.isNullOrEmpty(lastResultJson)) {
            try {
                lastResultCached = jsonAdapter.fromJson(lastResultJson);
            } catch (IOException e) {
                Timber.e(e);
                lastResultCached = new PingingTaskResult(true, 0);
            }
        }
        else
            lastResultCached = new PingingTaskResult(true, 0);
    }

    /**
     * A factory method for quick creation of a pinging task
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
     * Replaces the default pinger (for Unit tests)
     * @param pinger the class that implements {@link Pinger} and actually performs pinging
     */
    void setPinger(@NotNull Pinger pinger) {this.pinger = pinger;}

    @Override
    public String getType() {
        return TaskType.PINGING;
    }

    @Embedded
    public PingingTaskSettings settings;

    @Ignore
    @Inject
    JsonAdapter<PingingTaskResult> jsonAdapter; // package-private for Unit tests

    @Override
    @WorkerThread
    protected void doTheActualWork() {

        checkNotNull(settings);
        PingingTaskSettings pingSettings;

        synchronized (this) {
            pingSettings = settings.clone();
        }

        PingingTaskResult result = pinger.ping(pingSettings);

        formatAndSetResult(result);

        taskGotNewResult = true;
    }

    private void formatAndSetResult(@NotNull  PingingTaskResult result) {

        long nowMs = Instant.now().getMillis();

        if (result.pingOK) {
            result.lastSuccessTime = nowMs;
            if (lastResultCached.errorCode != TaskResult.NO_ERROR ||
                    lastResultCached.lastSuccessTime == 0) // If this is the first successful ping after creation or a failure
                result.firstSuccessTime = result.lastSuccessTime; // Memorize for uptime calculation
            result.uptimeMs = nowMs - result.firstSuccessTime;
        }
        else {
            result.lastSuccessTime = lastResultCached.lastSuccessTime;
            result.downtimeMs = nowMs - result.lastSuccessTime;
        }


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

/*    @Override
    public synchronized void copyPropertiesFrom(MonitoringTask sourceTask) {
        super.copyPropertiesFrom(sourceTask);
        if (sourceTask instanceof PingingTask) {
            settings = ((PingingTask)sourceTask).settings;
        }
    }*/
}
