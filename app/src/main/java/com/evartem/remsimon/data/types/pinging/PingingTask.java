package com.evartem.remsimon.data.types.pinging;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.evartem.remsimon.data.MonitoringTask;
import com.evartem.remsimon.data.TaskEntry;
import com.evartem.remsimon.data.types.TaskType;

import org.joda.time.Instant;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@Entity(tableName = TaskType.PINGING,
        indices = @Index({"taskEntryId"}),
        foreignKeys = @ForeignKey(entity = TaskEntry.class, parentColumns = "id", childColumns = "taskEntryId"))
public class PingingTask extends MonitoringTask {

    /**
     * Creates the pinging task.
     *
     * @param taskEntryId An id of the task from the {@link TaskEntry} table
     * @param description A short description of the task
     */
    @Ignore
    public PingingTask(@NonNull String taskEntryId, @NonNull String description) {
        this(taskEntryId, description, MonitoringTask.MODE_STOPPED, 0);
    }

    /**
     * SHOULD NOT be used directly in the app! A special constructor for the Room library.
     */
    public PingingTask(@NonNull String taskEntryId, @NonNull String description, int mode, long lastSuccessTime) {
        super(taskEntryId, description, mode, lastSuccessTime);
    }

    @Override
    public String getType() {
        return TaskType.PINGING;
    }

    @Embedded
    public PingingTaskSettings settings;

    @Override
    @WorkerThread
    public void doTheWork() {
        lastSuccessTime = Instant.now().getMillis();
    }


}
