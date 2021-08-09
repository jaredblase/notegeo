package com.mobdeve.s15.group5.notegeo.recyclebin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mobdeve.s15.group5.notegeo.models.NoteGeoRepository

class RecycleBinViewModel(private val repository: NoteGeoRepository) : ViewModel() {
    val deletedNotes = repository.deletedNotes.asLiveData()

    fun deleteAll() {
    }

    fun restoreAll() {

    }
}