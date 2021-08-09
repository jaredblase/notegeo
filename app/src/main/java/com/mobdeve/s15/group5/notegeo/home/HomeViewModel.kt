package com.mobdeve.s15.group5.notegeo.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mobdeve.s15.group5.notegeo.models.NoteGeoRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: NoteGeoRepository) : ViewModel() {
    val savedNotes = repository.savedNotes.asLiveData()
    val isGridView = MutableLiveData(true)

    fun toggleView() {
        isGridView.value = isGridView.value?.not()
    }

    fun recycleNotes(ids: List<Long>?) = viewModelScope.launch {
        ids?.let { repository.recycleNotes(it) }
    }
}