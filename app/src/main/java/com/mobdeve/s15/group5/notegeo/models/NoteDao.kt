package com.mobdeve.s15.group5.notegeo.models

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM note WHERE dateDeleted IS NULL")
    fun getSavedNotes(): Flow<MutableList<Note>>

    @Query("SELECT * FROM note WHERE dateDeleted IS NOT NULL")
    fun getDeletedNotes(): Flow<MutableList<Note>>

    @Insert
    suspend fun insert(vararg note: Note)

    @Update
    suspend fun update(vararg note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("DELETE FROM note WHERE dateDeleted IS NOT NULL")
    suspend fun emptyTrash()
}