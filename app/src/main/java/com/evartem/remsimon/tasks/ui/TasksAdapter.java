package com.evartem.remsimon.tasks.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.tasks.contractmvp.TasksPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TasksAdapter extends RecyclerView.Adapter<TaskViewHolder> {

    @Inject
    TasksPresenter presenter;

    @Inject
    TaskViewHolderFactory taskViewHolderFactory;

    List<MonitoringTask> tasks;

    @Inject
    TasksAdapter() {
        tasks = new ArrayList<>();
    }

    void updateTasks(List<MonitoringTask> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return taskViewHolderFactory.createViewHolder(parent, viewType);
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
        holder.bind(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks == null ? 0 : tasks.size();
    }

    @Override
    public int getItemViewType(int position) {
        return tasks.get(position).getTypeInt();
    }


}
