package com.mobdeve.s15.group5.notegeo.label

import android.content.Context
import androidx.lifecycle.*
import com.mobdeve.s15.group5.notegeo.databinding.ActivityLabelBinding
import com.mobdeve.s15.group5.notegeo.models.Label
import com.mobdeve.s15.group5.notegeo.models.NoteGeoRepository
import com.mobdeve.s15.group5.notegeo.toast
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LabelViewModel(
    private val repository: NoteGeoRepository,
    private val dispatcher: CoroutineDispatcher
) :
    ViewModel() {
    val repoLabels = repository.allLabels.asLiveData()
    val allLabels = MutableLiveData<List<Label>>()
    val numSelected = MutableLiveData(0)

    fun modifyNumSelected(isSelected: Boolean) {
        numSelected.value = numSelected.value?.plus(if (isSelected) 1 else -1)
    }

    fun updateLabel(label: Label) = viewModelScope.launch(Dispatchers.IO) {
        allLabels.value?.first { it._id == label._id }?.name = label.name
        repository.updateLabel(label)
    }

    fun deleteSelectedLabels() = viewModelScope.launch(dispatcher) {
        allLabels.value?.run {
            val (toDelete, toSave) = partition { it.isChecked.get() }

            repository.deleteLabels(toDelete.map { it._id })
            allLabels.value = toSave
        }
        numSelected.value = 0
    }

    fun addLabel(binding: ActivityLabelBinding, context: Context) =
        viewModelScope.launch(dispatcher) {
            val text = binding.addLabelEt.text.toString()
            // checks
            when {
                text.isBlank() -> {
                    context.toast("Cannot add blank label!")
                }
                allLabels.value?.any { it.name.equals(text, true) } == true -> {
                    context.toast("Label already exists!")
                }
                else -> {
                    val label = Label(name = text)
                    val id = repository.addLabel(label)
                    allLabels.value = allLabels.value?.plus(label.apply { _id = id.toInt() })
                    binding.labelCancelBtn.performClick()
                }
            }
        }
}
