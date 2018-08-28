package com.evartem.remsimon.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.UUID;

/**
 * A record of an available task in the app. Unlike concrete implementations of the MonitoringTask interface,
 * this Room table contains records of different types of tasks in the form of: taskID - taskType,
 * i.e. this is a register of all existing tasks (of all types).
 */
@Entity(tableName = "Tasks")
public final class TaskEntry {
    @PrimaryKey
    private final String id;

    private final String type;

    public TaskEntry(String taskType) {
        id = UUID.randomUUID().toString();
        this.type = taskType;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
