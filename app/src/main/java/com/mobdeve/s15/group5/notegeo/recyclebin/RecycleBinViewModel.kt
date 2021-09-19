package com.mobdeve.s15.group5.notegeo.recyclebin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mobdeve.s15.group5.notegeo.models.NoteGeoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class RecycleBinViewModel(
    private val repository: NoteGeoRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    val deletedNotes = repository.deletedNotes.asLiveData()

    fun cleanOldNotes() = viewModelScope.launch(dispatcher) {
        repository.cleanOldNotes()
    }

    fun delete(ids: List<Long>?) = viewModelScope.launch(dispatcher) {
        ids?.let { repository.deleteNotes(it) }
    }

    fun restore(ids: List<Long>?) = viewModelScope.launch(dispatcher) {
        if (ids != null) {
            deletedNotes.value?.run {
                forEach { if (it.note._id in ids) it.note.dateDeleted = null }
                repository.updateNotes(this)
            }
        }
    }

    fun deleteAll() = viewModelScope.launch(dispatcher) { repository.emptyTrash() }

    fun restoreAll() = viewModelScope.launch(dispatcher) {
        deletedNotes.value?.run {
            forEach { it.note.dateDeleted = null }
            repository.updateNotes(this)
        }
    }
}