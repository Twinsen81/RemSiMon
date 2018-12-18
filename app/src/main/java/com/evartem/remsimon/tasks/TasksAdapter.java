package com.evartem.remsimon.tasks;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evartem.remsimon.R;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.types.pinging.PingingTask;
import com.evartem.remsimon.data.types.pinging.PingingTaskResult;
import com.evartem.remsimon.tasks.ContractMVP.TasksPresenter;
import com.squareup.moshi.JsonAdapter;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    @Inject
    TasksPresenter presenter;

    List<MonitoringTask> tasks;

    @Inject
    JsonAdapter<PingingTaskResult> pingingTaskResultJsonAdapter;

    @Inject
    TasksAdapter() {}

    void updateTasks(List<MonitoringTask> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_pinging, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull TaskViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.itemView.setOnClickListener(v -> onItemClicked(holder.getAdapterPosition()));
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull TaskViewHolder holder) {
        holder.itemView.setOnClickListener(null);
        super.onViewDetachedFromWindow(holder);
    }

    private void onItemClicked(int adapterPosition) {
        presenter.onTaskClicked(tasks.get(adapterPosition));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {

        MonitoringTask vhTask = tasks.get(position);
        holder.tvName.setText(vhTask.getDescription());


        if (vhTask instanceof PingingTask) {
            PingingTask task = (PingingTask) vhTask;
            holder.tvAddress.setText(task.settings.getPingAddress());

            PingingTaskResult result = null;
            try {
                result = pingingTaskResultJsonAdapter.fromJson(tasks.get(position).getLastResultJson());
            } catch (IOException e) {
                Timber.wtf(e);
            }

            if (result != null) {
                holder.tvName.setBackgroundColor(holder.tvName.getContext().getResources().getColor(result.pingOK ? R.color.pingOk:R.color.pingNotOk));
                holder.tvUpDown.setText(result.pingOK ? R.string.uptime : R.string.downtime);
                holder.tvTime.setText(String.valueOf(result.pingOK ? result.uptimeMs : result.downtimeMs));
                holder.tvSuccessTime.setText(String.valueOf(result.lastSuccessTime));
            }
        }
    }

    @Override
    public int getItemCount() {
        //if (tasks != null) Timber.i("items number in the adapter: %s", tasks.size());
        return tasks == null ? 0 : tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

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

        TaskViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
