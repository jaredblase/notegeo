package com.mobdeve.s15.group5.notegeo.recyclebin

import android.os.Looper
import android.util.Log
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

    fun delete(ids: List<Long>?) {
        if (ids != null) {
            viewModelScope.launch(dispatcher) { repository.deleteNotes(ids) }
        }
    }

    fun restore(ids: List<Long>?) = viewModelScope.launch(dispatcher) {
        Log.d("IS IN MAIN", (Looper.myLooper() == Looper.getMainLooper()).toString())
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