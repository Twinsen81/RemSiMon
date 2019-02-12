package com.evartem.remsimon.tasks.contractmvp;

import com.evartem.remsimon.basemvp.presenter.Presenter;
import com.evartem.remsimon.data.types.base.MonitoringTask;

public interface TasksPresenter extends Presenter {

    void onTaskClicked(MonitoringTask task);

    void reloadTasks();
}
