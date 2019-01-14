package com.evartem.remsimon.tasks;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.evartem.remsimon.BaseMVP.presenter.BasePresenter;
import com.evartem.remsimon.data.TasksManager;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.tasks.ContractMVP.TasksPresenter;
import com.evartem.remsimon.tasks.ContractMVP.TasksView;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Provides the view with tasks to display, notifies it when tasks' representations should be updated with new data
 */
public class TasksPresenterImpl extends BasePresenter<TasksView> implements TasksPresenter, TasksManager.StateChangedListener {

    @Inject
    TasksManager manager;

    @Inject
    TasksPresenterImpl(TasksView view) {
        super(view);
    }

    @Override
    public void onTaskClicked(MonitoringTask task) {
        view.editTask(task);
    }

    /**
     * Gets the list of existing tasks. The result is delivered directly to the view.
     */
    @Override
    public void reloadTasks() {
        manager.getTasks(tasks -> {
            Timber.i("Loaded %s tasks from the data source", tasks.size());
            view.displayTasks(tasks);
        });
    }

    /**
     * Start listening on changes in the tasks when the view is ready to display them.
     */
    @Override
    public void onStart(@Nullable Bundle savedInstanceState) {
        super.onStart(savedInstanceState);

        Timber.i("View is ready: %s", view);
        manager.addTaskStateChangedListener(this);
    }

    /**
     * A notification about a change in the tasks list.
     * If a particular task has changed (a new result is available) then the view is commanded to redraw it.
     * On other changes - just reload tasks and draw them.
     * @param changedTask The task that has an update or null if the change is related to multiple tasks
     * @param taskPositionInTheList The position of the changedTask in the list of tasks
     * @param whatChanged The reason for the change
     */
    @Override
    public void onTaskStateChanged(@Nullable MonitoringTask changedTask, int taskPositionInTheList, int whatChanged) {
        if (changedTask != null && whatChanged == STATE_CHANGED) {
            if (taskPositionInTheList >= 0)
                view.displayChangedState(changedTask, taskPositionInTheList);
            else
                reloadTasks();
        }
    }

    /**
     * The view is being destroyed - unsubscribe from listening to changes and
     * tell the manager to flush all data to the datasource since the app will be probably closed soon.
     */
    @Override
    public void onEnd() {
        Timber.i("View is not ready: %s", view);
        manager.removeTaskStateChangedListener(this);

        manager.forceSaveAll2Datasource();

        super.onEnd();
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadTasks();
    }
}
