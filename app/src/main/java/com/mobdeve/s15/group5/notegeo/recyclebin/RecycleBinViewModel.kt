package com.mobdeve.s15.group5.notegeo.recyclebin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mobdeve.s15.group5.notegeo.models.NoteGeoRepository
import kotlinx.coroutines.launch
class RecycleBinViewModel(private val repository: NoteGeoRepository) : ViewModel() {
    val deletedNotes = repository.deletedNotes.asLiveData()

    fun delete(ids: List<Long>?) {
        if (ids != null) {
            viewModelScope.launch { repository.deleteNotes(ids) }
        }
    }

    fun restore(ids: List<Long>?) = viewModelScope.launch {
        if (ids != null) {
            deletedNotes.value?.run {
                forEach { if (it._id in ids) it.dateDeleted = null }
                repository.updateNotes(this)
            }
        }
    }

    fun deleteAll() = viewModelScope.launch { repository.emptyTrash() }

    fun restoreAll() = viewModelScope.launch {
        deletedNotes.value?.run {
            forEach { it.dateDeleted = null }
            repository.updateNotes(this)
        }
    }
}