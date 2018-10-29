package com.evartem.remsimon.tasks.ContractMVP;

import com.evartem.remsimon.BaseMVP.view.MVPView;
import com.evartem.remsimon.data.types.pinging.PingingTask;

public interface TasksView extends MVPView {

    void displayTask(PingingTask task);

    void displayMessage(String message);

    void displayResult(String result);
}
