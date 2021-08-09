package com.mobdeve.s15.group5.notegeo.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mobdeve.s15.group5.notegeo.models.NoteGeoRepository

class HomeViewModel(private val repository: NoteGeoRepository) : ViewModel() {
    val savedNotes = repository.savedNotes.asLiveData()
    val isGridView = MutableLiveData(true)

    fun toggleView() {
        isGridView.value = isGridView.value?.not()
    }
}