package com.evartem.remsimon.tasks.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.evartem.remsimon.BaseMVP.view.BaseViewFragment;
import com.evartem.remsimon.R;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.types.base.TaskType;
import com.evartem.remsimon.data.types.http.HttpTask;
import com.evartem.remsimon.data.types.pinging.PingingTask;
import com.evartem.remsimon.taskEdit.http.HttpTaskEditFragment;
import com.evartem.remsimon.taskEdit.pinging.PingingTaskEditFragment;
import com.evartem.remsimon.tasks.ContractMVP.TasksPresenter;
import com.evartem.remsimon.tasks.ContractMVP.TasksView;
import com.evartem.remsimon.tasks.UI.TasksAdapter;
import com.evartem.remsimon.util.Helper;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Displays the list of existing tasks.
 * Each task is rendered by the corresponding adapter.
 */
public class TasksFragment extends BaseViewFragment<TasksPresenter> implements TasksView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.rvTasks)
    RecyclerView rvTasks;

    /**
     * Supporting the swipe-down-to-refresh gesture
     */
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    @Inject
    TasksAdapter tasksAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tasks_fragment, container, false);
    }

    /**
     * When the view is ready..
     */
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        setupRecyclerView();
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    /**
     * Initializing the recycler view
     */
    private void setupRecyclerView() {
        rvTasks.setAdapter(tasksAdapter);
        rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @OnClick(R.id.fab)
    void onFabClick() {

        AlertDialog.Builder taskTypeSelectionDialog = new AlertDialog.Builder(getContext());
        taskTypeSelectionDialog.setTitle(R.string.taskTypeDialogCaption);
        Pair<List<String>, List<String>> typeNames = Helper.getAllTypes(getContext());

        taskTypeSelectionDialog.setItems(typeNames.second.toArray(new String[0]), (dialog, which) -> {
            createNewTask(typeNames.first.get(which));
        });
        taskTypeSelectionDialog.create().show();
    }


    private void createNewTask(String taskType) {
        if (taskType.equals(TaskType.PINGING))
            openFragment(new PingingTaskEditFragment());
        if (taskType.equals(TaskType.HTTP))
            openFragment(new HttpTaskEditFragment());
    }

    /**
     * Opening the corresponding fragment to edit the task of a certain type
     */
    @Override
    public void editTask(MonitoringTask task) {
        Fragment fragment = null;

        // PingingTask
        if (task instanceof PingingTask) {
            PingingTaskEditFragment taskEditFragment = new PingingTaskEditFragment();
            taskEditFragment.setTaskToEdit((PingingTask) task);
            fragment = taskEditFragment;
        }

        // HttpTask
        if (task instanceof HttpTask) {
            HttpTaskEditFragment taskEditFragment = new HttpTaskEditFragment();
            taskEditFragment.setTaskToEdit((HttpTask) task);
            fragment = taskEditFragment;
        }

        if (fragment != null)
            openFragment(fragment);
    }

    /**
     * A helper function for opening fragments
     */
    private void openFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Called by the presenter to display all tasks
     */
    @Override
    public void displayTasks(List<MonitoringTask> tasks) {
        tasksAdapter.updateTasks(tasks);
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Called by the presenter when a particular task has got a new result available for the user
     */
    @Override
    public void displayChangedState(MonitoringTask task, int positionInTheList) {
        if (tasksAdapter != null && positionInTheList >= 0)
            tasksAdapter.notifyItemChanged(positionInTheList); // Update just the task changed
    }

    /**
     * The user is refreshing manually
     */
    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        presenter.reloadTasks();
    }

    @Override
    public void onResume() {
        super.onResume();

        // The soft keyboard isn't always hidden after we return from the editing fragment,
        // so hide the keyboard here
        try {
            InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }
}
