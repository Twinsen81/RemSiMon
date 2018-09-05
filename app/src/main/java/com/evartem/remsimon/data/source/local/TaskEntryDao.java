package com.evartem.remsimon.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.evartem.remsimon.data.types.base.TaskEntry;

import java.util.List;

@Dao
public interface TaskEntryDao {

    @Query("SELECT * FROM tasks")
    List<TaskEntry> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addOrReplace(TaskEntry taskEntry);

    @Query("DELETE FROM tasks WHERE id = :taskId")
    int remove(@NonNull String taskId);
}
