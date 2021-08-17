package com.mobdeve.s15.group5.notegeo.models

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Central repository for the whole application
 */
class NoteGeoRepository(private val labelDao: LabelDao, private val noteDao: NoteDao) {
    val savedNotes: Flow<MutableList<NoteAndLabel>> = noteDao.getSavedNotes()
    val deletedNotes: Flow<MutableList<NoteAndLabel>> = noteDao.getDeletedNotes()
    val allLabels: Flow<MutableList<Label>> = labelDao.getAll()

    @WorkerThread
    suspend fun addLabel(label: Label) = labelDao.insert(label)[0]

    @WorkerThread
    suspend fun updateLabel(label: Label) {
        labelDao.update(label)
    }

    @WorkerThread
    suspend fun deleteLabels(ids: List<Int>) {
        labelDao.delete(ids)
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
    suspend fun updateNotes(notes: List<NoteAndLabel>) {
        noteDao.update(*notes.map { it.note }.toTypedArray())
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
