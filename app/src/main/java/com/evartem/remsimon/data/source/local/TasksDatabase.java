package com.evartem.remsimon.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.evartem.remsimon.data.types.http.HttpTask;
import com.evartem.remsimon.data.types.pinging.PingingTask;

@Database(entities = {PingingTask.class, HttpTask.class}, version = 1)
public abstract class TasksDatabase extends RoomDatabase {

    public abstract PingingTaskDao pingingTaskDao();
    public abstract HttpTaskDao httpTaskDao();
}
