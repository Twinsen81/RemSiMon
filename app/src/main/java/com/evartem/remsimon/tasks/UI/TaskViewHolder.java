package com.evartem.remsimon.tasks.UI;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.evartem.remsimon.data.types.base.MonitoringTask;

import butterknife.ButterKnife;

public abstract class TaskViewHolder extends RecyclerView.ViewHolder {
    TaskViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public abstract void bind(MonitoringTask monitoringTask);
}