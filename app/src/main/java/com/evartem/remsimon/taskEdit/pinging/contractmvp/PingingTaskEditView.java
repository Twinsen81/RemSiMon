package com.evartem.remsimon.taskEdit.pinging.contractmvp;

import com.evartem.remsimon.basemvp.view.MVPView;
import com.evartem.remsimon.data.types.pinging.PingingTask;

public interface PingingTaskEditView extends MVPView {

    /**
     * Sets the task whose properties will be edited
     */
    void setTaskToEdit(PingingTask task);
}
