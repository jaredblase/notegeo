package com.mobdeve.s15.group5.notegeo.models

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

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
    suspend fun updateNotes(notes: List<Note>) {
        noteDao.update(*notes.toTypedArray())
    }

    @WorkerThread
    suspend fun emptyTrash() = noteDao.emptyTrash()
}