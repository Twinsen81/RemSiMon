package com.evartem.remsimon.tasks;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.evartem.remsimon.R;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TasksActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TasksContract.View {

    @BindView(R.id.tvResult)
    TextView tvResult;
    @BindView(R.id.btnApply)
    Button btnApply;

    @BindView(R.id.etLabel)
    EditText etLabel;
    @BindView(R.id.etAddress)
    EditText etAddress;
    @BindView(R.id.etRunEveryMs)
    EditText etRunEveryMs;
    @BindView(R.id.etTimeoutMs)
    EditText etTimeoutMs;

    TasksContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }
        });*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ButterKnife.bind(this);

        setEditTextsCallbacks();

        btnApply.setOnClickListener(view -> presenter.onApplyClicked());

        presenter = new TasksPresenter();
        presenter.attachView(this);
        presenter.viewIsReady();
    }


    private void setEditTextsCallbacks() {
        /*etLabel.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (presenter.isInputValidTitle(((EditText) v).getText().toString().trim())) {
                    v.setBackgroundColor(Color.WHITE);
                } else {
                    v.setBackgroundColor(Color.RED);
                }
            } else
                v.setBackgroundColor(Color.WHITE);
        });
        etAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (presenter.isInputValidAddress(((EditText) v).getText().toString().trim())) {
                    v.setBackgroundColor(Color.WHITE);
                } else {
                    v.setBackgroundColor(Color.RED);
                }
            } else
                v.setBackgroundColor(Color.WHITE);
        });
        etTimeoutMs.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (presenter.isInputValidTimeoutMs(((EditText) v).getText().toString().trim())) {
                    ((EditText) v).setError(null);
                } else {
                    v.setBackgroundColor(Color.RED);
                }
            } else
                v.setBackgroundColor(Color.WHITE);
        });
        etRunEveryMs.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (presenter.isInputValidRunEveryMs(((EditText) v).getText().toString().trim())) {
                    v.setBackgroundColor(Color.WHITE);
                } else {
                    v.setBackgroundColor(Color.RED);
                }
            } else
                v.setBackgroundColor(Color.WHITE);

        });*/

        etLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                onInputChanged();
            }
        });
        etAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                onInputChanged();
            }
        });
        etTimeoutMs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                onInputChanged();
            }
        });
        etRunEveryMs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                onInputChanged();
            }
        });
    }

    private void onInputChanged() {

        boolean addressIsValid = presenter.isInputValidAddress(etAddress.getText().toString().trim());

        etAddress.setError(addressIsValid ? null : "Not an IP or URL!");

        btnApply.setEnabled(
                addressIsValid &&
                        presenter.isInputValidTitle(etLabel.getText().toString().trim()) &&
                        presenter.isInputValidRunEveryMs(etRunEveryMs.getText().toString().trim()) &&
                        presenter.isInputValidTimeoutMs(etTimeoutMs.getText().toString().trim()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        if (isFinishing()) {
            presenter.destroy();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.job_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void displayTaskData(String title, String address, String runEveryMs, String timeoutMs) {
        etLabel.setText(title);
        etAddress.setText(address);
        etRunEveryMs.setText(runEveryMs);
        etTimeoutMs.setText(timeoutMs);
    }

    @UiThread
    @Override
    public void displayMessage(@NotNull String message) {
        Snackbar.make(findViewById(R.id.tasksLayout), message, Snackbar.LENGTH_LONG).show();
    }

    @UiThread
    @Override
    public void displayResult(@NotNull String result) {
        tvResult.setText(result);
    }
}
