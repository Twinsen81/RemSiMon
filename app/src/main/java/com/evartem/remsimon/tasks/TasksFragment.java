package com.evartem.remsimon.tasks;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evartem.remsimon.BaseMVP.view.BaseViewFragment;
import com.evartem.remsimon.R;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.tasks.ContractMVP.TasksPresenter;
import com.evartem.remsimon.tasks.ContractMVP.TasksView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class TasksFragment extends BaseViewFragment<TasksPresenter> implements TasksView {

/*    @BindView(R.id.tvResult)
    TextView tvResult;


    @BindView(R.id.btnApply)
    Button btnApply;

    @BindView(R.id.etLabel)
    EditText etTitle;
    @BindView(R.id.etAddress)
    EditText etAddress;
    @BindView(R.id.etRunEveryMs)
    EditText etRunEveryMs;
    @BindView(R.id.etTimeoutMs)
    EditText etTimeoutMs;*/

    @BindView(R.id.rvTasks)
    RecyclerView rvTasks;

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
    }

    private void setupRecyclerView() {
        rvTasks.setAdapter(tasksAdapter);
        rvTasks.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void displayTasks(List<MonitoringTask> tasks) {
        tasksAdapter.updateTasks(tasks);
    }

    /*private void setEditTextsCallbacks() {

        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean titleIsValid = presenter.isInputValidTitle(etTitle.getText().toString().trim());
                etTitle.setError(titleIsValid ? null : "Must not be empty!");
                onInputChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean addressIsValid = presenter.isInputValidAddress(etAddress.getText().toString().trim());
                etAddress.setError(addressIsValid ? null : "Not an IP or URL!");
                onInputChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etTimeoutMs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean timeoutMsIsValid = presenter.isInputValidTimeoutMs(etTimeoutMs.getText().toString().trim());
                etTimeoutMs.setError(timeoutMsIsValid ? null : "Enter a valid number!");
                onInputChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etRunEveryMs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean runEveryMsIsValid = presenter.isInputValidRunEveryMs(etRunEveryMs.getText().toString().trim());
                etRunEveryMs.setError(runEveryMsIsValid ? null : "Enter a valid number!");
                onInputChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick(R.id.btnApply)
    void onApplyButtonClicked() {
        PingingTask currTask = presenter.getCurrentTask();
        currTask.setDescription(etTitle.getText().toString().trim());
        currTask.setRunTaskEveryMs(Integer.valueOf(etRunEveryMs.getText().toString().trim()));
        currTask.settings.setPingAddress(etAddress.getText().toString().trim());
        currTask.settings.setPingTimeoutMs(Integer.valueOf(etTimeoutMs.getText().toString().trim()));
        presenter.onApplyClicked(currTask);
    }

    private void onInputChanged() {

        boolean addressIsValid = presenter.isInputValidAddress(etAddress.getText().toString().trim());
        boolean titleIsValid = presenter.isInputValidTitle(etTitle.getText().toString().trim());
        boolean runEveryMsIsValid = presenter.isInputValidRunEveryMs(etRunEveryMs.getText().toString().trim());
        boolean timeoutMsIsValid = presenter.isInputValidTimeoutMs(etTimeoutMs.getText().toString().trim());

        btnApply.setEnabled(addressIsValid && titleIsValid && runEveryMsIsValid && timeoutMsIsValid);
    }

    @Override
    public void displayTask(PingingTask task) {
        etTitle.setText(task.getDescription());
        etAddress.setText(task.settings.getPingAddress());
        etRunEveryMs.setText(String.valueOf(task.getRunTaskEveryMs()));
        etTimeoutMs.setText(String.valueOf(task.settings.getPingTimeoutMs()));
    }

    @UiThread
    @Override
    public void displayMessage(@NotNull String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @UiThread
    @Override
    public void displayResult(@NotNull String result) {
        tvResult.setText(result);
    }

    @Override
    public void onResume() {
        super.onResume();
        onInputChanged();
    }*/
}
