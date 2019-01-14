package com.evartem.remsimon.taskEdit.pinging.ContractMVP;

import com.evartem.remsimon.BaseMVP.presenter.Presenter;
import com.evartem.remsimon.data.types.pinging.PingingTask;

public interface PingingTaskEditPresenter extends Presenter {

    // This set of methods verifies the correctness of the entered data
    boolean isInputValidTitle(String title);
    boolean isInputValidAddress(String address);
    boolean isInputValidRunEveryMs(String runEveryMs);
    boolean isInputValidTimeoutMs(String timeoutMs);

    /**
     * User wants to save the edited data
     * @param task The edited or newly created pinging task
     */
    void onApplyClicked(PingingTask task);

    /**
     * User wants to delete the task
     */
    void onDeleteClicked(PingingTask task);


}
