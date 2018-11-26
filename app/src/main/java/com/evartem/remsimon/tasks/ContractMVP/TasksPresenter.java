package com.evartem.remsimon.tasks.ContractMVP;

import com.evartem.remsimon.BaseMVP.presenter.Presenter;
import com.evartem.remsimon.data.types.base.MonitoringTask;

public interface TasksPresenter extends Presenter {

    void onTaskClicked(MonitoringTask task);

    void reloadTasks();
    //List<MonitoringTask> getTasks();
}
