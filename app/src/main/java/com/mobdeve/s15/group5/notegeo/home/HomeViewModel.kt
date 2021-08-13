package com.mobdeve.s15.group5.notegeo.home

import androidx.lifecycle.*
import com.mobdeve.s15.group5.notegeo.models.Note
import com.mobdeve.s15.group5.notegeo.models.NoteGeoRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: NoteGeoRepository) : ViewModel() {
    val savedNotes = repository.savedNotes.asLiveData()
    val isGridView = MutableLiveData(true)

    var lastSavedNote: Note? = null
    val postPosition = MutableLiveData<Int>()

    init {
        // gets the position of the last-saved note whenever the list is updated
        savedNotes.observeForever {
            for ((pos, elem) in it.withIndex()) {
                if (elem._id == lastSavedNote?._id) {
                    postPosition.value = pos
                    break
                }
            }
        }
    }

    fun toggleView() {
        isGridView.value = isGridView.value?.not()
    }

    fun recycleNotes(ids: List<Long>?) = viewModelScope.launch {
        ids?.let { repository.recycleNotes(it) }
    }

    fun recycleNote(id: Long) = viewModelScope.launch { repository.recycleNotes(listOf(id)) }

    fun upsertNote(note: Note) {
        lastSavedNote = note
        viewModelScope.launch {
            repository.upsertNote(note)
        }
    }
}