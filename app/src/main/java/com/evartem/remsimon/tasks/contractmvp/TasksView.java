package com.evartem.remsimon.tasks.contractmvp;

import com.evartem.remsimon.basemvp.view.MVPView;
import com.evartem.remsimon.data.types.base.MonitoringTask;

import java.util.List;

public interface TasksView extends MVPView {

    void displayTasks(List<MonitoringTask> tasks);

    void editTask(MonitoringTask task);

    void displayChangedState(MonitoringTask task, int positionInTheList);

}
