package com.evartem.remsimon.tasks;

import com.evartem.remsimon.BaseMVP.MvpPresenter;
import com.evartem.remsimon.BaseMVP.MvpView;

public interface TasksContract {

    interface View extends MvpView {

        void displayTaskData(String title, String address, String runEveryMs, String timeoutMs);

        void displayMessage(String message);

        void displayResult(String result);
    }

    interface Presenter extends MvpPresenter<View> {

        boolean isInputValidTitle(String title);
        boolean isInputValidAddress(String address);
        boolean isInputValidRunEveryMs(String runEveryMs);
        boolean isInputValidTimeoutMs(String timeoutMs);

        void onApplyClicked();
    }


}
