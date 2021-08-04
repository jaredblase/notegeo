package com.mobdeve.s15.group5.notegeo.label

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mobdeve.s15.group5.notegeo.models.Label
import com.mobdeve.s15.group5.notegeo.models.NoteGeoRepository
import com.mobdeve.s15.group5.notegeo.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class LabelViewModel(private val repository: NoteGeoRepository) : ViewModel() {
    val allLabels = repository.allLabels.asLiveData()
    val listIsEmpty = ObservableBoolean()

    fun updateLabels(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            allLabels.value?.run {
                // Filters empty labels before updating the database.
                repository.updateLabels(filter { it.label.isNotEmpty() })
            }
        }
        context.toast("Saved!")
    }

    fun deleteSelectedLabels(adapter: LabelAdapter) {
        viewModelScope.launch(Dispatchers.Default) {
            allLabels.value?.run {
                for (i in size - 1 downTo 0) {
                    if (this[i].isChecked.get()) {
                        removeAt(i)
                        launch(Dispatchers.Main) { adapter.notifyItemRemoved(i) }
                    }
                }
                listIsEmpty.set(isEmpty())
            }
        }
    }

    fun removeAllLabels(adapter: LabelAdapter) {
        allLabels.value?.run {
            val len = this.size
            clear()
            adapter.notifyItemRangeRemoved(0, len)
        }
        listIsEmpty.set(true)
    }

    fun addLabel(adapter: LabelAdapter) {
        allLabels.value?.add(0, Label())
        adapter.notifyItemInserted(0)
        listIsEmpty.set(false)
    }
}

class LabelViewModelFactory(private val repository: NoteGeoRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LabelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LabelViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}