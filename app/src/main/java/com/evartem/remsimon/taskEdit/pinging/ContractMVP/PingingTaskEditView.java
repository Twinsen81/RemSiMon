package com.evartem.remsimon.taskEdit.pinging.ContractMVP;

import com.evartem.remsimon.BaseMVP.view.MVPView;
import com.evartem.remsimon.data.types.pinging.PingingTask;

public interface PingingTaskEditView extends MVPView {

    /**
     * Sets the task whose properties will be edited
     */
    void setTaskToEdit(PingingTask task);
}
