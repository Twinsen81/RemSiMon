package com.evartem.remsimon.taskEdit.http.ContractMVP;

import com.evartem.remsimon.BaseMVP.presenter.Presenter;
import com.evartem.remsimon.data.types.http.HttpTask;

public interface HttpTaskEditPresenter extends Presenter {

    // This set of methods verifies the correctness of the entered data
    boolean isInputValidTitle(String title);
    boolean isInputValidAddress(String address);
    boolean isInputValidRunEveryMs(String runEveryMs);
    boolean isInputValidHistoryDepth(String historyDepth);

    /**
     * User wants to save the edited data
     * @param task The edited or newly created pinging task
     */
    void onApplyClicked(HttpTask task);

    /**
     * User wants to delete the task
     */
    void onDeleteClicked(HttpTask task);


}
