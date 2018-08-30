package com.evartem.remsimon.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.UUID;

/**
 * A record of an available task in the app. Unlike concrete implementations of the MonitoringTask interface,
 * this Room table contains records of different types of tasks in the form of: taskID - taskType,
 * i.e. this is a register of all existing tasks (of all types).
 */
@Entity(tableName = "Tasks")
public final class TaskEntry {
    @PrimaryKey
    @NonNull
    private final String id;

    @NonNull
    private final String type;

    @Ignore
    public TaskEntry(@NonNull  String type) {
        this(UUID.randomUUID().toString(), type);
    }

    public TaskEntry(@NonNull String id, @NonNull String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
