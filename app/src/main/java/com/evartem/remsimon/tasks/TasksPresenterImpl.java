package com.evartem.remsimon.tasks;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.evartem.remsimon.BaseMVP.presenter.BasePresenter;
import com.evartem.remsimon.data.TasksManager;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.types.pinging.HybridPinger;
import com.evartem.remsimon.data.types.pinging.PingingTask;
import com.evartem.remsimon.tasks.ContractMVP.TasksPresenter;
import com.evartem.remsimon.tasks.ContractMVP.TasksView;
import com.google.common.base.Strings;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import timber.log.Timber;

public class TasksPresenterImpl extends BasePresenter<TasksView> implements TasksPresenter, TasksManager.StateChangedListener {

    @Inject
    TasksManager manager;

    private PingingTask theTask = null;

    @Inject
    TasksPresenterImpl(TasksView view) {
        super(view);
    }

    @Override
    public boolean isInputValidTitle(String title) {
        return !Strings.isNullOrEmpty(title);
    }

    @Override
    public boolean isInputValidAddress(String address) {
        return !Strings.isNullOrEmpty(address) && HybridPinger.isValidUrl(address);
    }

    @Override
    public boolean isInputValidRunEveryMs(String runEveryMs) {
        try {
            int ms = Integer.valueOf(runEveryMs);
            if (ms > 0 && ms < 604800000) // 1 week
                return true;
        } catch (NumberFormatException ignored) {
        }
        return false;
    }

    @Override
    public boolean isInputValidTimeoutMs(String timeoutMs) {
        try {
            int ms = Integer.valueOf(timeoutMs);
            if (ms > 0 && ms < 10000)
                return true;
        } catch (NumberFormatException ignored) {
        }
        return false;
    }

    @Override
    public void onApplyClicked(@NotNull PingingTask task) {
        manager.addTask(theTask);
        manager.forceSaveAll2Datasource();
    }

    @Override
    public PingingTask getCurrentTask() {
        return theTask;
    }

    @Override
    public void onStart(@Nullable Bundle savedInstanceState) {
        super.onStart(savedInstanceState);

        Timber.i("View is ready: %s", view);
        manager.getTasks(tasks -> {
            if (tasks.size() > 0) {
                Timber.i("Loaded a task from DS: %s", tasks.get(0).getDescription());
                theTask = (PingingTask) tasks.get(0);
            } else
                theTask = new PingingTask("New task", MonitoringTask.MODE_ACTIVE);
            view.displayTask(theTask);
        });
        manager.addTaskStateChangedListener(this);
    }

    @Override
    public void onTaskStateChanged(@Nullable MonitoringTask changedTask, int whatChanged) {
        if (changedTask != null) {
            view.displayResult(changedTask.getLastResultJson());
        }
    }

    @Override
    public void onEnd() {
        Timber.i("View is not ready: %s", view);
        manager.removeTaskStateChangedListener(this);

        super.onEnd();
    }

}
