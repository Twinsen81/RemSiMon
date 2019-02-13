package com.evartem.remsimon.tasks.ui;

import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evartem.remsimon.TheApp;
import com.evartem.remsimon.di.AppComponent;
import com.evartem.remsimon.R;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.types.pinging.PingingTask;
import com.evartem.remsimon.data.types.pinging.PingingTaskResult;
import com.squareup.moshi.JsonAdapter;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import timber.log.Timber;

import static com.evartem.remsimon.util.Helper.formatDateTime;
import static com.evartem.remsimon.util.Helper.formatPeriod;

public class PingingViewHolder extends TaskViewHolder {

    @Inject
    JsonAdapter<PingingTaskResult> pingingTaskResultJsonAdapter;

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

    @BindView(R.id.tvLastDowntime)
    TextView tvLastDowntime;

    private PingingViewHolder(View itemView, AppComponent appComponent) {
        super(itemView);
        appComponent.inject(this);
    }

    public static PingingViewHolder createViewHolder(@NonNull ViewGroup parent, AppComponent appComponent) {
        return new PingingViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_pinging, parent, false),
                appComponent);
    }

    @Override
    public void bind(MonitoringTask monitoringTask) {
        if (!(monitoringTask instanceof PingingTask))
            throw new RuntimeException("Wrong ViewHolder type binding. Expected: PingingTask, actual: " + monitoringTask.getClass().getSimpleName());

        PingingTask task = (PingingTask) monitoringTask;

        tvName.setText(task.getDescription());
        Resources res = tvName.getContext().getResources();

        tvAddress.setText(task.settings.getPingAddress());

        PingingTaskResult result = null;
        String resultJson = task.getLastResultJson();

        try {
            if (!resultJson.trim().isEmpty())
                result = pingingTaskResultJsonAdapter.fromJson(resultJson);
            else
                result = new PingingTaskResult(true, 0);
        } catch (IOException e) {
            Timber.wtf(e);
        }

        if (result != null) {
            int color = R.color.pingOk;
            if (!TheApp.isInternetConnectionAvailable)
                color = R.color.pingNoInternet;
            else {
                if (result.isPingOK() && !result.lastPingOK) color = R.color.pingNotOkAttempts;
                if (!result.isPingOK()) color = R.color.pingNotOk;
            }
            GradientDrawable drawable = (GradientDrawable) tvName.getBackground();
            drawable.setColor(res.getColor(color));

            tvUpDown.setText(result.isPingOK() ? R.string.uptime : R.string.downtime);
            tvTime.setText(formatPeriod(result.isPingOK() ? result.uptimeMs : result.downtimeMs, res));
            tvSuccessTime.setText(formatDateTime(result.lastSuccessTime, res));
            tvLastDowntime.setVisibility(result.isPingOK() && result.lastDowntimeDurationMs != 0 ? View.VISIBLE : View.GONE);
            tvLastDowntime.setText(String.format(res.getText(R.string.lastDowntimeLasted).toString(),
                    formatPeriod(result.lastDowntimeDurationMs, res), formatDateTime(result.downtimeEndedTime, res)));
        }
    }
}
