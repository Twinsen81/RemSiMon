package com.evartem.remsimon.data.types.http;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.types.base.TaskResult;
import com.evartem.remsimon.data.types.base.TaskType;
import com.evartem.remsimon.data.types.pinging.HybridPinger;
import com.evartem.remsimon.data.types.pinging.Pinger;
import com.evartem.remsimon.data.types.pinging.PingingTaskResult;
import com.evartem.remsimon.data.types.pinging.PingingTaskSettings;
import com.google.common.base.Strings;
import com.squareup.moshi.JsonAdapter;

import org.jetbrains.annotations.NotNull;
import org.joda.time.Instant;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Response;
import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Issues a GET HTTP request to the provided URL,
 * on receiving the JSON data in the response,
 * extracts values of the provided fields
 */
@Entity(tableName = TaskType.HTTP,
        indices = @Index({"taskId"}))
public class HttpTask extends MonitoringTask {

    @Embedded
    public HttpTaskSettings settings;

    @Ignore
    JsonAdapter<HttpTaskResult> jsonAdapter; // package-private for Unit tests

    @Ignore
    @Inject
    GeneralApi httpApi;

    /**
     * Creates the http task.
     *
     * @param description A short description of the task
     */
    @Ignore
    public HttpTask(@NonNull String description) {
        this(description, MonitoringTask.MODE_STOPPED, "");
        settings = new HttpTaskSettings();
    }

    @Ignore
    public HttpTask(@NonNull String description, int mode) {
        this(description, mode, "");
        settings = new HttpTaskSettings();
    }

    /**
     * SHOULD NOT be used directly in the app! A special constructor for the Room library.
     */
    public HttpTask(@NonNull String description, int mode, String lastResultJson) {
        super(description, mode, lastResultJson);

        jsonAdapter = moshi.adapter(HttpTaskResult.class);

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
     * A factory method for quick creation of the HttpTask
     */
    public static HttpTask create(@NonNull String description, int runTaskEveryMs,
                                  @NonNull String address, @NonNull String fileds) {
        HttpTask task = new HttpTask(description);
        task.setRunTaskEveryMs(runTaskEveryMs);
        task.settings.setHttpAddress(address);
        task.settings.setFields(fileds);
        return task;
    }

    @Override
    public String getType() {
        return TaskType.HTTP;
    }

    @Override
    @WorkerThread
    protected void doTheActualWork() {

        checkNotNull(settings);
        HttpTaskSettings httpSettings;

        synchronized (this) {
            httpSettings = settings.clone();
        }

        Response<ResponseBody> response = null;
        String exceptionText = "";

        try {
            response = httpApi.getHttpData(httpSettings.getHttpAddress()).execute();
        } catch (IOException e) {
            exceptionText = e.getMessage();
        }

        formatAndSetResult(response, exceptionText, httpSettings);

        taskGotNewResult = true;
    }

    private void formatAndSetResult(Response<ResponseBody> response, String exceptionText, HttpTaskSettings httpSettings) {

        HttpTaskResult result;

        if (!(lastResultCached instanceof HttpTaskResult)) // This is the first result we get
            result = new HttpTaskResult();
        else
            result = (HttpTaskResult) lastResultCached;

        if (response == null || exceptionText.length() > 0 || !response.isSuccessful()) {
            result.responseOK = false;
            result.errorMessage = getErrorMessage(response, exceptionText);
        }else
        {
            result.responseOK = true;
            result.responses = getResponses(result.responses, response, httpSettings);
        }

        if (result.responseOK)
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

    private List<String> getResponses(List<String> responses, Response<ResponseBody> response, HttpTaskSettings httpSettings) {
        responses.add(response.body().toString());
        while (responses.size() > 1 && responses.size() > httpSettings.getHistoryDepth())
            responses.remove(0);
        return  responses;
    }

    private String getErrorMessage(Response<ResponseBody> response, String exceptionText) {
        if (exceptionText.length() > 0)
            return exceptionText;
        else if (response != null)
            return response.toString();
        return "WTF? Very strange error without any description (((";
    }

    @NonNull
    synchronized HttpTaskResult getLastResult() { //Package access for unit tests
        return (HttpTaskResult) lastResultCached;
    }


    @Override
    public synchronized void copyPropertiesFrom(MonitoringTask sourceTask) {
        super.copyPropertiesFrom(sourceTask);
        if (sourceTask instanceof HttpTask) {
            jsonAdapter = ((HttpTask) sourceTask).jsonAdapter;
            settings = ((HttpTask) sourceTask).settings;
        }
    }
}
