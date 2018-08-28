package com.evartem.remsimon.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = TaskType.PINGING,
        indices = @Index({"id"}),
        foreignKeys = @ForeignKey(entity = TaskEntry.class, parentColumns = "id", childColumns = "id"))
public class PingingTask implements MonitoringTask {

    @PrimaryKey
    private final String id;
    private String description;
    private volatile boolean active = false;


    public PingingTask(@NonNull String taskEntryId, @NonNull String description) {
        this.id = taskEntryId;
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(@NonNull String description) {
        if (description != null)
            this.description = description;
        else
            this.description = "";
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String getStatusText() {
        return (active ? "Active: " : "Not active: ") + getDescription();
    }

    @Override
    public String getType() {
        return TaskType.PINGING;
    }

    @Override
    public String getId() {
        return null;
    }
}
