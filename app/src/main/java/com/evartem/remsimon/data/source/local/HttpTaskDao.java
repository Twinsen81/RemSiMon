package com.evartem.remsimon.data.source.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import com.evartem.remsimon.data.types.http.HttpTask;

import java.util.List;

@Dao
public interface HttpTaskDao {

    @Query("SELECT * FROM httptask")
    List<HttpTask> getAll();

    @Query("SELECT * FROM httptask WHERE taskId = :taskId")
    HttpTask getById(@NonNull String taskId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addOrReplace(@NonNull HttpTask task);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addOrReplaceAll(@NonNull List<HttpTask> tasks);

    @Query("DELETE FROM httptask WHERE taskId = :taskId")
    int deleteById(@NonNull String taskId);

    @Query("DELETE FROM httptask")
    int deleteAll();

}
