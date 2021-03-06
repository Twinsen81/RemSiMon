package com.evartem.remsimon.data.source.local;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.evartem.remsimon.TheApp;
import com.evartem.remsimon.data.types.base.MonitoringTask;
import com.evartem.remsimon.data.source.TasksDataSource;
import com.evartem.remsimon.data.types.http.HttpTask;
import com.evartem.remsimon.data.types.pinging.PingingTask;
import com.evartem.remsimon.util.AppExecutors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * The Room database as the TaskDataSource implementation
 */
public class TasksLocalDataSource implements TasksDataSource {

    /**
     * Executors that will be used to run IO and UI operations
     */
    @Inject
    AppExecutors executors;

    @Inject
    TheApp app;

    /**
     * The Room's DAO object for accessing the corresponding DB
     */
    @Inject
    PingingTaskDao pingingTaskDao;

    @Inject
    HttpTaskDao httpTaskDao;

    @Inject
    TasksLocalDataSource() {}

    @Override
    public void getTasks(@NonNull LoadTasksListener callback) {
        executors.diskIO().execute(() -> {
            List<MonitoringTask> tasks = getTasksSync();
            executors.mainThread().execute(() -> callback.onTasksLoaded(tasks));
        });
    }

    @Override
    @UiThread
    public void updateOrAddTask(@NonNull MonitoringTask task) {
        executors.diskIO().execute(() -> updateOrAddTasksByType(Arrays.asList(task)));
    }

    @Override
    public void updateOrAddTasks(@NonNull List<MonitoringTask> tasks) {
        if (isOnMainThread())
            executors.diskIO().execute(() -> updateOrAddTasksByType(tasks));
        else
            updateOrAddTasksByType(tasks);
    }

    private boolean isOnMainThread() {
        return Looper.getMainLooper().getThread().equals(Thread.currentThread());
    }

    ////////////////////////////////////////////////////////////////////////////
    // The following methods should be modified when a new task type is added

    @Override
    @WorkerThread
    public List<MonitoringTask> getTasksSync() {
        List<MonitoringTask> tasks = new ArrayList<>();

        // PingingTask
        List<PingingTask> pingingTasks = pingingTaskDao.getAll();
        if (pingingTasks != null) {
            for (PingingTask task : pingingTasks) task.injectDependencies(app.getAppComponent());
            tasks.addAll(pingingTasks);
        }

        // HttpTask
        List<HttpTask> httpTask = httpTaskDao.getAll();
        if (httpTask != null) {
            for (HttpTask task : httpTask) task.injectDependencies(app.getAppComponent());
            tasks.addAll(httpTask);
        }

        return tasks;
    }

    @WorkerThread
    private void updateOrAddTasksByType(@NonNull List<MonitoringTask> tasks) {
        for (MonitoringTask task :
                tasks) {

            // PingingTask
            if (task instanceof PingingTask)
                pingingTaskDao.addOrReplace((PingingTask)task);

            // HttpTask
            if (task instanceof HttpTask)
                httpTaskDao.addOrReplace((HttpTask)task);
        }
    }

    @Override
    @UiThread
    public void deleteAllTasks() {
        executors.diskIO().execute(() -> pingingTaskDao.deleteAll());
        executors.diskIO().execute(() -> httpTaskDao.deleteAll());
    }

    @Override
    @UiThread
    public void deleteTask(@NonNull MonitoringTask task) {
        executors.diskIO().execute(() -> deleteTaskByType(task));
    }

    @WorkerThread
    private void deleteTaskByType(MonitoringTask task) {
        // PingingTask
        if (task instanceof PingingTask)
            pingingTaskDao.deleteById(task.getTaskId());

        // HttpTask
        if (task instanceof HttpTask)
            httpTaskDao.deleteById(task.getTaskId());
    }

}
