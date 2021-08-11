package com.mobdeve.s15.group5.notegeo.models

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Central repository for the whole application
 */
class NoteGeoRepository(private val labelDao: LabelDao, private val noteDao: NoteDao) {
    val savedNotes: Flow<MutableList<Note>> = noteDao.getSavedNotes()
    val deletedNotes: Flow<MutableList<Note>> = noteDao.getDeletedNotes()
    val allLabels: Flow<MutableList<Label>> = labelDao.getAll()

    @WorkerThread
    suspend fun updateLabels(labels: List<Label>) {
        labelDao.clearTable()
        labelDao.insert(*labels.toTypedArray())
    }

    @WorkerThread
    suspend fun recycleNotes(ids: List<Long>) {
        noteDao.recycleNotes(ids, Date())
    }

    @WorkerThread
    suspend fun deleteNotes(ids: List<Long>) {
        noteDao.delete(ids)
    }

    @WorkerThread
    suspend fun upsertNote(note: Note) {
        noteDao.insert(note)
    }

    @WorkerThread
    suspend fun updateNotes(notes: List<Note>) {
        noteDao.update(*notes.toTypedArray())
    }

    @WorkerThread
    suspend fun cleanOldNotes() {
        noteDao.cleanOldNotes(Date().time)
    }

    @WorkerThread
    suspend fun emptyTrash() {
        noteDao.emptyTrash()
    }
}
