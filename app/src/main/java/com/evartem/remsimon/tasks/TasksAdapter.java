package com.evartem.remsimon.tasks;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evartem.remsimon.R;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.tasks.ContractMVP.TasksPresenter;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {

    @Inject
    TasksPresenter presenter;

    List<MonitoringTask> tasks;

    @Inject
    public TasksAdapter() {
    }

    public void updateTasks(List<MonitoringTask> tasks) {
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
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.tvName.setText(tasks.get(position).getDescription());
        holder.tvResult.setText(tasks.get(position).getLastResultJson());
    }

    @Override
    public int getItemCount() {
        if (tasks != null) Timber.i("items number in the adapter: %s", tasks.size());
        return tasks == null ? 0 : tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvName)
        TextView tvName;

        @BindView(R.id.tvResult)
        TextView tvResult;

        TaskViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick({R.id.tvName, R.id.tvResult})
        public void onTaskClicked() {

        }


    }
}
