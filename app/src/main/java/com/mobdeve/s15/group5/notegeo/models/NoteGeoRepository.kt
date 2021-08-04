package com.mobdeve.s15.group5.notegeo.models

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

/**
 * Central repository for the whole application
 */
class NoteGeoRepository(private val labelDao: LabelDao) {
    val allLabels: Flow<MutableList<Label>> = labelDao.getAll()

    @WorkerThread
    suspend fun updateLabels(labels: List<Label>) {
        labelDao.clearTable()
        labelDao.insert(*labels.toTypedArray())
    }
}