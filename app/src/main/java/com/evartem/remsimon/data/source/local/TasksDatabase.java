package com.evartem.remsimon.data.source.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.evartem.remsimon.data.types.pinging.PingingTask;

@Database(entities = {PingingTask.class}, version = 1)
public abstract class TasksDatabase extends RoomDatabase {

    public abstract PingingTaskDao pingingTaskDao();
}
