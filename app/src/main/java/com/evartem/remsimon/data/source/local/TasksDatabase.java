package com.evartem.remsimon.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.evartem.remsimon.data.PingingTask;
import com.evartem.remsimon.data.TaskEntry;

@Database(entities = {TaskEntry.class, PingingTask.class}, version = 1)
public abstract class TasksDatabase extends RoomDatabase {

    public abstract TaskEntryDao taskEntryDao();

    public abstract PingingTaskDao pingingTaskDao();

    private static volatile TasksDatabase INSTANCE = null;

    public static TasksDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (TasksDatabase.class) {
                if (INSTANCE == null)
                    INSTANCE = Room.databaseBuilder(context, TasksDatabase.class, "MonitoringTasks.db").build();
            }
        }
        return INSTANCE;
    }
}
