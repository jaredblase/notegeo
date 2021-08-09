package com.mobdeve.s15.group5.notegeo.home

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mobdeve.s15.group5.notegeo.models.NoteGeoRepository

class HomeViewModel(private val repository: NoteGeoRepository) : ViewModel() {
    val savedNotes = repository.savedNotes.asLiveData()
    val isListEmpty = ObservableBoolean()
    val isGridView = ObservableBoolean()

    fun toggleView() = isGridView.set(!isGridView.get())
}