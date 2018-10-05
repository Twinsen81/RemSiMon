package com.evartem.remsimon.tasks;

import com.evartem.remsimon.BaseMVP.PresenterBase;
import com.evartem.remsimon.data.types.pinging.HybridPinger;
import com.google.common.base.Strings;

public class TasksPresenter extends PresenterBase<TasksContract.View> implements TasksContract.Presenter {
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
    public void onApplyClicked() {
        getView().displayMessage("APPLYING...");
        // TODO: create/update in the manager
    }

    @Override
    public void viewIsReady() {
        // TODO: load from DB (manager)
    }
}
