package com.evartem.remsimon.taskEdit.pinging;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.evartem.remsimon.basemvp.view.BaseViewFragment;
import com.evartem.remsimon.R;
import com.evartem.remsimon.TheApp;
import com.evartem.remsimon.data.types.pinging.PingingTask;
import com.evartem.remsimon.taskEdit.pinging.contractmvp.PingingTaskEditPresenter;
import com.evartem.remsimon.taskEdit.pinging.contractmvp.PingingTaskEditView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A fragment for entering new or editing existing data for a pinging task.
 * The entered data is verified for consistency by the presenter.
 */
public class PingingTaskEditFragment extends BaseViewFragment<PingingTaskEditPresenter> implements PingingTaskEditView {

    @BindView(R.id.btnApply)
    Button btnApply;
    @BindView(R.id.btnDelete)
    Button btnDelete;
    @BindView(R.id.etLabel)
    EditText etTitle;
    @BindView(R.id.etAddress)
    EditText etAddress;
    @BindView(R.id.etRunEveryMs)
    EditText etRunEveryMs;
    @BindView(R.id.etTimeoutMs)
    EditText etTimeoutMs;
    @BindView(R.id.etAttempts)
    EditText etAttempts;

    private static String SAVED_INSTANCE_TASK_ID = "taskId";

    /**
     * The task being edited or null if a new task is being created
     */
    PingingTask task;

    @Inject
    TheApp app;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pinging_task_edit_fragment, container, false);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        setEditTextsCallbacks();

        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_INSTANCE_TASK_ID)) {
            task = presenter.getTaskById(savedInstanceState.getString(SAVED_INSTANCE_TASK_ID));
        } else
            displayTaskToEdit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (task != null)
            outState.putString(SAVED_INSTANCE_TASK_ID, task.getTaskId());
    }

    /**
     * Fill the UI-elements with data - existing, in case of editing an existing task,
     * or default values - if this is a new task being created
     */
    private void displayTaskToEdit() {
        if (task != null) {
            etTitle.setText(task.getDescription());
            etAddress.setText(task.settings.getPingAddress());
            etRunEveryMs.setText(String.valueOf(task.getRunTaskEveryMs()));
            etTimeoutMs.setText(String.valueOf(task.settings.getPingTimeoutMs()));
            etAttempts.setText(String.valueOf(task.settings.getDowntimeFailedPingsNumber()));
            btnDelete.setEnabled(true);
        } else {
            etTitle.setText("Google DNS");
            etAddress.setText("8.8.8.8");
            etRunEveryMs.setText("10000");
            etTimeoutMs.setText("2000");
            etAttempts.setText("2");
        }
    }

    /**
     * Sets the task whose properties will be edited
     */
    @Override
    public void setTaskToEdit(PingingTask task) {
        this.task = task;
    }

    /**
     * Setting callbacks for edit fields.
     * The callbacks call the presenter's corresponding user input verification methods.
     */
    private void setEditTextsCallbacks() {
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

    /**
     * On "APPLY" click:
     * 1. Create a new instance of the pinging task if needed
     * 2. Fill the instance's fields with new data
     * 3. Notify the presenter
     * 4. Return to previous fragment
     */
    @OnClick(R.id.btnApply)
    void onApplyButtonClicked() {
        if (task == null) // Creating a new task (not editing an existing one)
        {
            task = new PingingTask("A pinging task");
            //app.getAppComponent().inject(task);
            task.injectDependencies(app.getAppComponent());
        }
        task.setDescription(etTitle.getText().toString().trim());
        task.setRunTaskEveryMs(Integer.valueOf(etRunEveryMs.getText().toString().trim()));
        task.settings.setPingAddress(etAddress.getText().toString().trim());
        task.settings.setPingTimeoutMs(Integer.valueOf(etTimeoutMs.getText().toString().trim()));
        task.settings.setDowntimeFailedPingsNumber(Integer.valueOf(etAttempts.getText().toString().trim()));
        presenter.onApplyClicked(task);
        getFragmentManager().popBackStack();
    }

    /**
     * On "DELETE" click:
     * 1. Notify the presenter
     * 2. Return to previous fragment
     */
    @OnClick(R.id.btnDelete)
    void onDeleteButtonClicked() {
        presenter.onDeleteClicked(task);
        getFragmentManager().popBackStack();
    }

    @OnClick(R.id.btnCancel)
    void onCancelButtonClicked() {
        getFragmentManager().popBackStack();
    }

    /**
     * Make sure the "APPLY" button is clickable only if the entered data is consistent
     */
    private void onInputChanged() {
        boolean addressIsValid = presenter.isInputValidAddress(etAddress.getText().toString().trim());
        boolean titleIsValid = presenter.isInputValidTitle(etTitle.getText().toString().trim());
        boolean runEveryMsIsValid = presenter.isInputValidRunEveryMs(etRunEveryMs.getText().toString().trim());
        boolean timeoutMsIsValid = presenter.isInputValidTimeoutMs(etTimeoutMs.getText().toString().trim());
        btnApply.setEnabled(addressIsValid && titleIsValid && runEveryMsIsValid && timeoutMsIsValid);
    }


    @Override
    public void onResume() {
        super.onResume();
        onInputChanged(); // Disable the "APPLY" button for a new task
    }
}
