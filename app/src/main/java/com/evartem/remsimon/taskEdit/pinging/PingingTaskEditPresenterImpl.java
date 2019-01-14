package com.evartem.remsimon.taskEdit.pinging;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.evartem.remsimon.BaseMVP.presenter.BasePresenter;
import com.evartem.remsimon.data.TasksManager;
import com.evartem.remsimon.data.types.pinging.HybridPinger;
import com.evartem.remsimon.data.types.pinging.PingingTask;
import com.evartem.remsimon.taskEdit.pinging.ContractMVP.PingingTaskEditPresenter;
import com.evartem.remsimon.taskEdit.pinging.ContractMVP.PingingTaskEditView;
import com.google.common.base.Strings;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Verifies the correctness of user input.
 * Adds/updates/deletes the task in question at the manager.
 */
public class PingingTaskEditPresenterImpl extends BasePresenter<PingingTaskEditView> implements PingingTaskEditPresenter {

    @Inject
    TasksManager manager;

    @Inject
    PingingTaskEditPresenterImpl(PingingTaskEditView view) {
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
        manager.addTask(task);
        manager.forceSaveAll2Datasource();
    }


    @Override
    public void onDeleteClicked(PingingTask task) {
        manager.deleteTask(task);
        manager.forceSaveAll2Datasource();
    }

    @Override
    public void onEnd() {
        Timber.i("View is not ready: %s", view);

        super.onEnd();
    }

}
