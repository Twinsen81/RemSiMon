package com.evartem.remsimon.taskEdit.http.ContractMVP;

import com.evartem.remsimon.BaseMVP.view.MVPView;
import com.evartem.remsimon.data.types.http.HttpTask;

public interface HttpTaskEditView extends MVPView {

    /**
     * Sets the task whose properties will be edited
     */
    void setTaskToEdit(HttpTask task);
}
