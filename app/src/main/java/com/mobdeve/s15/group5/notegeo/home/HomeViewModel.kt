package com.mobdeve.s15.group5.notegeo.home

import android.content.Context
import androidx.lifecycle.*
import com.mobdeve.s15.group5.notegeo.alarms.AlarmReceiver
import com.mobdeve.s15.group5.notegeo.models.Note
import com.mobdeve.s15.group5.notegeo.models.NoteGeoRepository
import com.mobdeve.s15.group5.notegeo.noteview.NoteAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: NoteGeoRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    val savedNotes = repository.savedNotes.asLiveData()
    val isGridView = MutableLiveData(true)
    val filterReminders = MutableLiveData(false)

    var lastSavedNote: Note? = null
    val postPosition = MutableLiveData<Int>()

    init {
        // gets the position of the last-saved note whenever the list is updated
        savedNotes.observeForever {
            for ((pos, elem) in it.withIndex()) {
                if (elem.note._id == lastSavedNote?._id) {
                    postPosition.value = pos
                    break
                }
            }
        }
    }

    fun toggleView() {
        isGridView.value = isGridView.value?.not()
    }

    fun recycleNotes(ids: List<Long>?, context: Context) = viewModelScope.launch(dispatcher) {
        ids?.let {
            savedNotes.value?.forEach { noteAndLabel ->
                if (noteAndLabel.note._id in it) {
                    AlarmReceiver().cancelAlarm(context, noteAndLabel.note)
                }
            }
            repository.recycleNotes(it)
        }
    }

    fun recycleNote(id: Long) =
        viewModelScope.launch(dispatcher) { repository.recycleNotes(listOf(id)) }

    fun upsertNote(note: Note) {
        lastSavedNote = note
        viewModelScope.launch(dispatcher) {
            repository.upsertNote(note)
        }
    }

    fun filterNotes(adapter: NoteAdapter, query: CharSequence?) =
        viewModelScope.launch(dispatcher) {
            val temp = savedNotes.value?.run {
                if (filterReminders.value!!) savedNotes.value!!.filter { it.note.hasReminders } else this
            }

            if (!query.isNullOrEmpty()) {
                temp?.filter {
                    it.note.title.contains(query, true) ||
                    it.note.body.contains(query, true) ||
                    it.label?.name?.contains(query, true) == true
                }
            }

            launch(Dispatchers.Main) {
                adapter.submitList(temp)
            }
        }
}