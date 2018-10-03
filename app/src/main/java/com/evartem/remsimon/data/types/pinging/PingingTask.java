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
import com.google.common.base.Strings;
import com.squareup.moshi.JsonAdapter;

import org.jetbrains.annotations.NotNull;
import org.joda.time.Instant;

import java.io.IOException;

import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;


@Entity(tableName = TaskType.PINGING,
        indices = @Index({"taskId"}))
        //foreignKeys = @ForeignKey(entity = TaskEntry.class, parentColumns = "id", childColumns = "taskId"))
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

    /**
     * SHOULD NOT be used directly in the app! A special constructor for the Room library.
     */
    public PingingTask(@NonNull String description, int mode, String lastResultJson) {
        super(description, mode, lastResultJson);

        pinger = new HybridPinger();
        jsonAdapter = moshi.adapter(PingingTaskResult.class);

        // Empty lastResultCached is created in the base class, but if we have already a JSON-serialized result in the Room -> unpack it
        if (!Strings.isNullOrEmpty(lastResultJson)) {
            try {
                lastResultCached = jsonAdapter.fromJson(lastResultJson);
            } catch (IOException e) {
                Timber.e(e);
                lastResultCached = new TaskResult();
            }
        }
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
        checkNotNull(lastResultCached);

        if (result.pingOK)
            result.lastSuccessTime = Instant.now().getMillis();
        else
            result.lastSuccessTime = lastResultCached.lastSuccessTime;

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

    @NonNull
    synchronized PingingTaskResult getLastResult() { //Package access for unit tests
        return (PingingTaskResult)lastResultCached;
    }


    @Override
    public synchronized void copyPropertiesFrom(MonitoringTask sourceTask) {
        super.copyPropertiesFrom(sourceTask);
        if (sourceTask instanceof PingingTask) {
            jsonAdapter = ((PingingTask)sourceTask).jsonAdapter;
            settings = ((PingingTask)sourceTask).settings;
        }
    }
}
