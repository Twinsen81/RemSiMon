package com.evartem.remsimon.tasks.ui;

import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evartem.remsimon.TheApp;
import com.evartem.remsimon.data.types.http.HttpTaskSettings;
import com.evartem.remsimon.di.AppComponent;
import com.evartem.remsimon.R;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.types.http.HttpTask;
import com.evartem.remsimon.data.types.http.HttpTaskResult;
import com.squareup.moshi.JsonAdapter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import timber.log.Timber;

import static com.evartem.remsimon.util.Helper.formatDateTime;

public class HttpViewHolder extends TaskViewHolder {

    @Inject
    JsonAdapter<HttpTaskResult> httpTaskResultJsonAdapter;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvResults)
    TextView tvResults;

    @BindView(R.id.tvSuccessTime)
    TextView tvSuccessTime;

    private HttpViewHolder(View itemView, AppComponent appComponent) {
        super(itemView);
        appComponent.inject(this);
    }

    static HttpViewHolder createViewHolder(@NonNull ViewGroup parent, AppComponent appComponent) {
        return new HttpViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_http, parent, false),
                appComponent);
    }

    @Override
    public void bind(MonitoringTask monitoringTask) {
        if (!(monitoringTask instanceof HttpTask))
            throw new RuntimeException("Wrong ViewHolder type binding. Expected: HttpTask, actual: "
                    + monitoringTask.getClass().getSimpleName());

        HttpTask task = (HttpTask) monitoringTask;

        HttpTaskResult result = null;
        String resultJson = task.getLastResultJson();

        try {
            if (!resultJson.trim().isEmpty())
                result = httpTaskResultJsonAdapter.fromJson(resultJson);
            else
                result = new HttpTaskResult();
        } catch (IOException e) {
            Timber.wtf(e);
        }

        if (result != null) {
            setTitle(result.responseOK, task.getDescription());
            tvSuccessTime.setText(formatDateTime(result.lastSuccessTime, itemView.getResources()));
            printResults(result, task.settings, task.getRunTaskEveryMs());
        }
    }

    private void setTitle(boolean responseOK, String description) {
        int color = R.color.pingOk;
        if (!TheApp.isInternetConnectionAvailable)
            color = R.color.pingNoInternet;
        else if (!responseOK) color = R.color.pingNotOk;
        GradientDrawable drawable = (GradientDrawable) tvName.getBackground();
        drawable.setColor(itemView.getResources().getColor(color));
        tvName.setText(description);
    }

    /**
     * Formats the string to display the current results of the task in the form
     * of "[field] = [value]". For results with the history depth > 1, also prints
     * the timestamp of the value (calculated based on the settings of the task)
     */
    private void printResults(HttpTaskResult result,  HttpTaskSettings settings, int runEveryMs) {
        StringBuilder resultBuilder = new StringBuilder();
        if (settings.getHistoryDepth() < 2) // Only one value per each field
        {
            for (Map.Entry<String, List<String>> entry:
                 result.responses.entrySet()) {

                if (resultBuilder.length() > 0) resultBuilder.append("\n");
                resultBuilder.append(entry.getKey());
                resultBuilder.append(" = ");
                if (!entry.getValue().isEmpty()) resultBuilder.append(entry.getValue().get(0));

            }
            tvResults.setTextSize(20);
        }else
        {
            for (Map.Entry<String, List<String>> entry:
                result.responses.entrySet()) {

            if (resultBuilder.length() > 0) resultBuilder.append("\n");
            resultBuilder.append("<< ");
            resultBuilder.append(entry.getKey());
            resultBuilder.append(" >>\n");
            int depth = entry.getValue().size();
                for (String value:
                     entry.getValue()) {
                    // TODO: save real timestamps in the HttpTask upon receiving the response
                    resultBuilder.append(formatDateTime(result.lastSuccessTime - runEveryMs * depth, itemView.getResources()));
                    resultBuilder.append(":   ");
                    resultBuilder.append(value);
                    resultBuilder.append("\n");
                    depth--;
                }
        }
            tvResults.setTextSize(14);
        }

        tvResults.setText(resultBuilder.toString().trim());
    }
}
