    package com.evartem.remsimon.tasks;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evartem.remsimon.BaseMVP.view.BaseViewFragment;
import com.evartem.remsimon.R;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.types.pinging.PingingTask;
import com.evartem.remsimon.taskEdit.TaskEditFragment;
import com.evartem.remsimon.tasks.ContractMVP.TasksPresenter;
import com.evartem.remsimon.tasks.ContractMVP.TasksView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

    public class TasksFragment extends BaseViewFragment<TasksPresenter> implements TasksView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.rvTasks)
    RecyclerView rvTasks;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @Inject
    TasksAdapter tasksAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tasks_fragment, container, false);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        setupRecyclerView();
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void setupRecyclerView() {
        rvTasks.setAdapter(tasksAdapter);
        rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @OnClick(R.id.fab)
    void onFabClick() {
        openEditTaskFragment(new TaskEditFragment());
    }

    @Override
    public void editTask(MonitoringTask task) {
        Fragment fragment = null;

        if (task instanceof PingingTask) {
            TaskEditFragment taskEditFragment = new TaskEditFragment();
            taskEditFragment.setTaskToEdit((PingingTask)task);
            fragment = taskEditFragment;
        }
        if (fragment != null)
            openEditTaskFragment(fragment);
    }

    private void openEditTaskFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void displayTasks(List<MonitoringTask> tasks) {
        tasksAdapter.updateTasks(tasks);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        presenter.reloadTasks();
    }


}
