package com.evartem.remsimon.taskEdit.ContractMVP;

import com.evartem.remsimon.BaseMVP.view.MVPView;
import com.evartem.remsimon.data.types.pinging.PingingTask;

public interface TaskEditView extends MVPView {

    void setTaskToEdit(PingingTask task);

}
