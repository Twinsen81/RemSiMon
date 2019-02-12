package com.evartem.remsimon.taskEdit.http;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.evartem.remsimon.basemvp.presenter.BasePresenter;
import com.evartem.remsimon.data.TasksManager;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.types.http.HttpTask;
import com.evartem.remsimon.data.types.pinging.HybridPinger;
import com.evartem.remsimon.taskEdit.http.contractmvp.HttpTaskEditPresenter;
import com.evartem.remsimon.taskEdit.http.contractmvp.HttpTaskEditView;
import com.google.common.base.Strings;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Verifies the correctness of user input.
 * Adds/updates/deletes the task in question at the manager.
 */
public class HttpTaskEditPresenterImpl extends BasePresenter<HttpTaskEditView> implements HttpTaskEditPresenter {

    @Inject
    TasksManager manager;

    @Inject
    HttpTaskEditPresenterImpl(HttpTaskEditView view) {
        super(view);
    }

    @Override
    public void onStart(@Nullable Bundle savedInstanceState) {
        super.onStart(savedInstanceState);
        Timber.i("View is ready: %s", view);
        //view.displayTask(new PingingTask("A pinging task"));
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
    public boolean isInputValidFields(String fields) {
        return !Strings.isNullOrEmpty(fields);
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
    public boolean isInputValidHistoryDepth(String historyDepth) {
        try {
            int hd = Integer.valueOf(historyDepth);
            if (hd > 0 && hd < 50)
                return true;
        } catch (NumberFormatException ignored) {
        }
        return false;
    }

    @Override
    public void onApplyClicked(@NotNull HttpTask task) {
        manager.addTask(task);
        manager.forceSaveAll2Datasource();
    }


    @Override
    public void onDeleteClicked(HttpTask task) {
        manager.deleteTask(task);
        manager.forceSaveAll2Datasource();
    }

    @Override
    @Nullable
    public HttpTask getTaskById(String taskId) {
        MonitoringTask task = manager.getTaskById(taskId);
        if (task instanceof HttpTask)
            return (HttpTask)task;
        return null;
    }

    @Override
    public void onEnd() {
        Timber.i("View is not ready: %s", view);

        super.onEnd();
    }

}
