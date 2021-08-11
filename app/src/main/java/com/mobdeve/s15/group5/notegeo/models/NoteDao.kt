package com.mobdeve.s15.group5.notegeo.models

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface NoteDao {
    @Query("SELECT * FROM note WHERE dateDeleted IS NULL ORDER BY isPinned DESC")
    fun getSavedNotes(): Flow<MutableList<Note>>

    @Query("SELECT * FROM note WHERE dateDeleted IS NOT NULL ORDER BY dateDeleted DESC")
    fun getDeletedNotes(): Flow<MutableList<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg note: Note)

    @Update
    suspend fun update(vararg note: Note)

    @Query("UPDATE note SET dateDeleted = :date, isPinned = 0 WHERE _id IN (:ids)")
    suspend fun recycleNotes(ids: List<Long>, date: Date)

    @Query("DELETE FROM note WHERE _id IN (:ids)")
    suspend fun delete(ids: List<Long>)

    @Query("DELETE FROM note WHERE dateDeleted IS NOT NULL")
    suspend fun emptyTrash()

    @Query("DELETE FROM note WHERE dateDeleted IS NOT NULL AND (:today - dateDeleted >= 2592000000 OR _id NOT IN (SELECT _id FROM note WHERE dateDeleted IS NOT NULL ORDER BY dateDeleted DESC LIMIT 15))")
    suspend fun cleanOldNotes(today: Long)
}