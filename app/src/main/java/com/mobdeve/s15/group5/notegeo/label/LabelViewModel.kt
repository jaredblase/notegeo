package com.mobdeve.s15.group5.notegeo.label

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mobdeve.s15.group5.notegeo.models.Label
import com.mobdeve.s15.group5.notegeo.models.NoteGeoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LabelViewModel(private val repository: NoteGeoRepository) : ViewModel() {
    val allLabels = repository.allLabels.asLiveData()
    val listIsEmpty = ObservableBoolean()

    fun updateLabel(label: Label) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateLabel(label)
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
