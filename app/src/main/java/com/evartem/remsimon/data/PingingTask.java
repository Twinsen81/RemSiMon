package com.evartem.remsimon.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.common.base.Strings;

import org.joda.time.Instant;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@Entity(tableName = TaskType.PINGING,
        indices = @Index({"taskEntryId"}),
        foreignKeys = @ForeignKey(entity = TaskEntry.class, parentColumns = "id", childColumns = "taskEntryId"))
public class PingingTask implements MonitoringTask {

    @PrimaryKey
    @NonNull
    private final String taskEntryId;

    private String description;

    private String pingAddress;

    private int everyNSeconds;

    private volatile int state = MonitoringTask.STATE_STOPPED;

    private volatile long lastSuccessTime;


    @Ignore
    public PingingTask(@NonNull String taskEntryId, @NonNull String description, @NonNull String pingAddress) {
        this(taskEntryId, description, pingAddress, MonitoringTask.STATE_STOPPED, 0);
    }

    public PingingTask(@NonNull String taskEntryId, @NonNull String description, @NonNull String pingAddress, int state, long lastSuccessTime) {
        this.taskEntryId = taskEntryId;
        this.description = description;
        this.pingAddress = pingAddress;
        this.state = state;
        this.lastSuccessTime = lastSuccessTime;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(@NonNull String description) {
        if (Strings.isNullOrEmpty(description)) {
            this.description = "New " + getType();
        } else
            this.description = description;

    }

    @Override
    public synchronized void activate() {
        state = MonitoringTask.STATE_ACTIVE;
        lastSuccessTime = Instant.now().getMillis();
    }

    @Override
    public synchronized void deactivate() {
        state = MonitoringTask.STATE_DEACTIVATED;
    }

    @Override
    public synchronized int getState() {
        return state;
    }

    @Override
    public synchronized String getStateText() {
        return (state == STATE_ACTIVE ? "Active: " : "Not active: ") + getDescription();
    }

    @Override
    public String getType() {
        return TaskType.PINGING;
    }

    @Override
    public String getTaskEntryId() {
        return taskEntryId;
    }


    public synchronized long getLastSuccessTime() {
        return lastSuccessTime;
    }

    @Override
    public synchronized Instant getLastSuccessInstant() {
        return new Instant(lastSuccessTime);
    }

    public synchronized String getPingAddress() {
        return pingAddress;
    }

    public synchronized void setPingAddress(@NonNull String pingAddress) {
        this.pingAddress = pingAddress;
    }

    public synchronized int getEveryNSeconds() {
        return everyNSeconds;
    }

    public synchronized void setEveryNSeconds(int everyNSeconds) {
        this.everyNSeconds = everyNSeconds;
    }

    @Override
    public synchronized void shutdown() {

    }

    @Override
    public boolean isStateChange() {
        return false;
    }

    @Override
    public void doTheWork() {

    }

    /*@Override
    public void copySettings(@NonNull MonitoringTask source) {
        if (source instanceof PingingTask) {

        }
    }*/
}
