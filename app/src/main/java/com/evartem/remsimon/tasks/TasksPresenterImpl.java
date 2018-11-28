package com.evartem.remsimon.tasks;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.evartem.remsimon.BaseMVP.presenter.BasePresenter;
import com.evartem.remsimon.data.TasksManager;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.types.pinging.PingingTask;
import com.evartem.remsimon.tasks.ContractMVP.TasksPresenter;
import com.evartem.remsimon.tasks.ContractMVP.TasksView;

import javax.inject.Inject;

import timber.log.Timber;

public class TasksPresenterImpl extends BasePresenter<TasksView> implements TasksPresenter, TasksManager.StateChangedListener {

    @Inject
    TasksManager manager;

    @Inject
    TasksPresenterImpl(TasksView view) {
        super(view);
    }

    @Override
    public void onTaskClicked(MonitoringTask task) {
        view.editTask(task);
    }

    @Override
    public void reloadTasks() {
        manager.getTasks(tasks -> {
            Timber.i("Loaded %s tasks from the data source", tasks.size());
            view.displayTasks(tasks);
        });
    }

    @Override
    public void onStart(@Nullable Bundle savedInstanceState) {
        super.onStart(savedInstanceState);

        Timber.i("View is ready: %s", view);
        manager.addTaskStateChangedListener(this);
    }

    @Override
    public void onTaskStateChanged(@Nullable MonitoringTask changedTask, int taskPositionInTheList, int whatChanged) {
        if (changedTask != null && whatChanged == STATE_CHANGED) {
            if (taskPositionInTheList >= 0)
                view.displayChangedState(changedTask, taskPositionInTheList);
            else
                reloadTasks();
        }
    }

    @Override
    public void onEnd() {
        Timber.i("View is not ready: %s", view);
        manager.removeTaskStateChangedListener(this);

        super.onEnd();
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.i("OnResume");
        reloadTasks();
    }
}
