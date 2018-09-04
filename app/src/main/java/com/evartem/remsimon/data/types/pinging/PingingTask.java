package com.evartem.remsimon.data.types.pinging;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Patterns;

import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.types.base.TaskEntry;
import com.evartem.remsimon.data.types.base.TaskResult;
import com.evartem.remsimon.data.types.base.TaskType;
import com.google.common.base.Strings;
import com.squareup.moshi.JsonAdapter;
import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.ping.PingResult;

import org.jetbrains.annotations.NotNull;
import org.joda.time.Instant;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;


@Entity(tableName = TaskType.PINGING,
        indices = @Index({"taskEntryId"}),
        foreignKeys = @ForeignKey(entity = TaskEntry.class, parentColumns = "id", childColumns = "taskEntryId"))
public class PingingTask extends MonitoringTask {

    /**
     * Creates the pinging task.
     *
     * @param taskEntryId An id of the task from the {@link TaskEntry} table
     * @param description A short description of the task
     */
    @Ignore
    public PingingTask(@NonNull String taskEntryId, @NonNull String description) {
        this(taskEntryId, description, MonitoringTask.MODE_STOPPED, "");
        settings = new PingingTaskSettings();
    }

    /**
     * SHOULD NOT be used directly in the app! A special constructor for the Room library.
     */
    public PingingTask(@NonNull String taskEntryId, @NonNull String description, int mode, String lastResultJson) {
        super(taskEntryId, description, mode, lastResultJson);
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

    @Override
    public String getType() {
        return TaskType.PINGING;
    }

    @Embedded
    public PingingTaskSettings settings;

    @Ignore
    private JsonAdapter<PingingTaskResult> jsonAdapter;

    @Override
    @WorkerThread
    protected void doTheActualWork() {

        checkNotNull(settings);
        PingingTaskSettings pingSettings;

        synchronized (this) {
            pingSettings = settings.clone();
        }

        PingingTaskResult result = ping(pingSettings);

        formatAndSetResult(result);
    }

    private PingingTaskResult ping(PingingTaskSettings pingSettings) {
        PingingTaskResult workResult = new PingingTaskResult();

        if (!isValidUrl(pingSettings.getPingAddress())) {
            workResult.errorCode = PingingTaskResult.ERROR_INVALID_ADDRESS;
            workResult.errorMessage = "Not a valid URL";
            return workResult;
        }

        PingResult pingResult;
        try {
            pingResult = Ping.onAddress(pingSettings.getPingAddress()).setTimeOutMillis(pingSettings.getPingTimeoutMs()).doPing();
        } catch (UnknownHostException e) {
            workResult.errorCode = PingingTaskResult.ERROR_INVALID_ADDRESS;
            workResult.errorMessage = "Unknown host";
            return workResult;
        }

        workResult.pingOK = pingResult.isReachable;
        workResult.pingTimeMs = Float.valueOf(pingResult.timeTaken * 1000).longValue();
        workResult.errorMessage = pingResult.getError();

        return workResult;
    }

    /**
     * This is used to check the given URL is valid or not.
     * @param url
     * @return true if url is valid, false otherwise.
     */
    private boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
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
        }
        lastResultCached = result;
    }
}
