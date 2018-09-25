package com.evartem.remsimon.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.support.annotation.NonNull;

import com.evartem.remsimon.data.types.pinging.PingingTask;

import java.util.List;

@Dao
public interface PingingTaskDao {

    @Query("SELECT * FROM pingingtask")
    List<PingingTask> getAll();

    @Query("SELECT * FROM pingingtask WHERE taskId = :taskId")
    PingingTask getById(@NonNull String taskId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addOrReplace(@NonNull PingingTask task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addOrReplaceAll(@NonNull List<PingingTask> tasks);

    @Query("DELETE FROM pingingtask WHERE taskId = :taskId")
    int deleteById(@NonNull String taskId);

    @Query("DELETE FROM pingingtask")
    int deleteAll();

    //@Update
    //int update(@NonNull PingingTask task);
}
