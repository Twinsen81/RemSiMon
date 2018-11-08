package com.evartem.remsimon.taskEdit.ContractMVP;

import com.evartem.remsimon.BaseMVP.presenter.Presenter;
import com.evartem.remsimon.data.types.pinging.PingingTask;

public interface TasksPresenter extends Presenter {

    boolean isInputValidTitle(String title);
    boolean isInputValidAddress(String address);
    boolean isInputValidRunEveryMs(String runEveryMs);
    boolean isInputValidTimeoutMs(String timeoutMs);

    void onApplyClicked(PingingTask task);

    PingingTask getCurrentTask();
}
