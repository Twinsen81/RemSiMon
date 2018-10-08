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

public class TasksPresenter extends PresenterBase<TasksContract.View> implements
        TasksContract.Presenter, TasksManager.StateChangedListener {

    TasksManager manager;

    PingingTask theTask = null;

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
        TheApp.getTM().addTask(theTask);
    }

    @Override
    public PingingTask getCurrentTask() {
        return theTask;
    }

    @Override
    public void viewIsReady() {

        manager = TheApp.getTM();

        manager.getTasks(tasks -> {
            if (tasks.size() > 0) {
                theTask = (PingingTask) tasks.get(0);
                getView().displayTask(theTask);
            } else
                theTask = new PingingTask("New task", MonitoringTask.MODE_ACTIVE);
        });

        manager.addTaskStateChangedListener(this);
    }

    @Override
    public void onTaskStateChanged(@Nullable MonitoringTask changedTask, int whatChanged) {
        if (changedTask != null) {
            getView().displayResult(changedTask.getLastResultJson());
        }
    }

    @Override
    public void destroy() {
        TheApp.getTM().forceSaveAll2Datasource();
        super.destroy();
    }


}
