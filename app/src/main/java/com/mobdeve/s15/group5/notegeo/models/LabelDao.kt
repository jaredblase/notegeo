package com.mobdeve.s15.group5.notegeo.models

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {
    @Query("SELECT * FROM label")
    fun getAll(): Flow<MutableList<Label>>

    @Insert
    suspend fun insert(vararg label: Label)

    @Query("DELETE FROM label")
    suspend fun clearTable()
}