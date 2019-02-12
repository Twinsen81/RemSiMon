package com.evartem.remsimon.tasks.ui;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.evartem.remsimon.di.scopes.PerApplication;
import com.evartem.remsimon.TheApp;
import com.evartem.remsimon.data.types.base.TaskType;

import javax.inject.Inject;


@PerApplication
public class TaskViewHolderFactory {

    @Inject
    TheApp app;

    @Inject
    public TaskViewHolderFactory()
    {
    }

    public TaskViewHolder createViewHolder(@NonNull ViewGroup parent, int viewType) {
        TaskViewHolder viewHolder;
        switch (viewType) {
            case TaskType.PINGING_INT:
                viewHolder = PingingViewHolder.createViewHolder(parent, app.getAppComponent());
                break;
            case TaskType.HTTP_INT:
                viewHolder = HttpViewHolder.createViewHolder(parent, app.getAppComponent());
                break;
            default:
                throw new RuntimeException("Unknown task type: " + viewType);
        }
        return viewHolder;
    }

}
