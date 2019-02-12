package com.evartem.remsimon.tasks.ui;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evartem.remsimon.di.AppComponent;
import com.evartem.remsimon.R;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.types.http.HttpTask;
import com.evartem.remsimon.data.types.http.HttpTaskResult;
import com.squareup.moshi.JsonAdapter;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import timber.log.Timber;

import static com.evartem.remsimon.util.Helper.formatDateTime;

public class HttpViewHolder extends TaskViewHolder {

    @Inject
    JsonAdapter<HttpTaskResult> httpTaskResultJsonAdapter;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvAddress)
    TextView tvAddress;

    @BindView(R.id.tvUpDown)
    TextView tvUpDown;

    @BindView(R.id.tvTime)
    TextView tvTime;

    @BindView(R.id.tvSuccessTime)
    TextView tvSuccessTime;

    private HttpViewHolder(View itemView, AppComponent appComponent) {
        super(itemView);
        appComponent.inject(this);
    }

    public static HttpViewHolder createViewHolder(@NonNull ViewGroup parent, AppComponent appComponent) {
        return new HttpViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_http, parent, false),
                appComponent);
    }

    @Override
    public void bind(MonitoringTask monitoringTask) {
        if (!(monitoringTask instanceof HttpTask))
            throw new RuntimeException("Wrong ViewHolder type binding. Expected: HttpTask, actual: " + monitoringTask.getClass().getSimpleName());

        HttpTask task = (HttpTask) monitoringTask;

        tvName.setText(task.getDescription());
        Resources res = tvName.getContext().getResources();

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
            tvAddress.setText(result.responses.toString());
            tvName.setBackgroundColor(res.getColor(result.responseOK ? R.color.pingOk : R.color.pingNotOk));
            tvUpDown.setText("");
            tvTime.setText("");
            tvSuccessTime.setText(formatDateTime(result.lastSuccessTime, res));
        }
    }
}
