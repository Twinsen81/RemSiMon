package com.evartem.remsimon.tasks;

import android.support.annotation.Nullable;

import com.evartem.remsimon.BaseMVP.PresenterBase;
import com.evartem.remsimon.TheApp;
import com.evartem.remsimon.data.TasksManager;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.types.pinging.HybridPinger;
import com.evartem.remsimon.data.types.pinging.PingingTask;
import com.google.common.base.Strings;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class TasksPresenter extends PresenterBase<TasksContract.View> implements
        TasksContract.Presenter, TasksManager.StateChangedListener {

    private TasksManager manager;

    private PingingTask theTask = null;

    public TasksPresenter() {
        manager = TheApp.getTM();
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

    /**
     * Read task from the data source or create a new one if there's none there
     */
    @Override
    public void viewIsReady() {
        Timber.i("View is ready: %s", getView());
        manager.getTasks(tasks -> {
            if (tasks.size() > 0) {
                Timber.i("Loaded a task from DS: %s", tasks.get(0).getDescription());
                theTask = (PingingTask) tasks.get(0);
            } else
                theTask = new PingingTask("New task", MonitoringTask.MODE_ACTIVE);
                        getView().displayTask(theTask);
        });
        manager.addTaskStateChangedListener(this);
    }

    @Override
    public void viewIsNotReady() {
        Timber.i("View is not ready: %s", getView());
        manager.removeTaskStateChangedListener(this);
    }

    @Override
    public void onTaskStateChanged(@Nullable MonitoringTask changedTask, int whatChanged) {
        if (changedTask != null) {
            getView().displayResult(changedTask.getLastResultJson());
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }


}
