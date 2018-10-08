package com.evartem.remsimon.tasks;

import com.evartem.remsimon.BaseMVP.MvpPresenter;
import com.evartem.remsimon.BaseMVP.MvpView;
import com.evartem.remsimon.data.types.pinging.PingingTask;

public interface TasksContract {

    interface View extends MvpView {

        void displayTask(PingingTask task);

        void displayMessage(String message);

        void displayResult(String result);
    }

    interface Presenter extends MvpPresenter<View> {

        boolean isInputValidTitle(String title);
        boolean isInputValidAddress(String address);
        boolean isInputValidRunEveryMs(String runEveryMs);
        boolean isInputValidTimeoutMs(String timeoutMs);

        void onApplyClicked(PingingTask task);

        PingingTask getCurrentTask();
    }


}
