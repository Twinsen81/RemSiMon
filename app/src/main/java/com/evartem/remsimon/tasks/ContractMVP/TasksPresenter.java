package com.evartem.remsimon.tasks.ContractMVP;

import com.evartem.remsimon.BaseMVP.presenter.Presenter;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.types.pinging.PingingTask;

import java.util.List;

public interface TasksPresenter extends Presenter {

    void onTaskClicked(MonitoringTask task);

    List<MonitoringTask> getTasks();
}
