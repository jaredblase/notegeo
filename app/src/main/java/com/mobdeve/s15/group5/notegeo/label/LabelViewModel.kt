package com.mobdeve.s15.group5.notegeo.label

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mobdeve.s15.group5.notegeo.databinding.ActivityLabelBinding
import com.mobdeve.s15.group5.notegeo.models.Label
import com.mobdeve.s15.group5.notegeo.models.NoteGeoRepository
import com.mobdeve.s15.group5.notegeo.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LabelViewModel(private val repository: NoteGeoRepository) : ViewModel() {
    val allLabels = repository.allLabels.asLiveData()
    val numSelected = MutableLiveData(0)

    fun modifyNumSelected(isSelected: Boolean) {
        numSelected.value = numSelected.value?.plus(if (isSelected) 1 else -1)
    }

    fun updateLabel(label: Label) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateLabel(label)
    }

    fun deleteSelectedLabels() = viewModelScope.launch {
        allLabels.value?.filter { it.isChecked.get() }?.map { it._id }?.let {
            repository.deleteLabels(it)
        }
        numSelected.value = 0
    }

    fun addLabel(binding: ActivityLabelBinding, context: Context) =
        viewModelScope.launch {
            val text = binding.addLabelEt.text.toString()
            // checks
            when {
                text.isBlank() -> {
                    context.toast("Cannot add blank label!")
                }
                allLabels.value?.any { it.label.equals(text, true) } == true -> {
                    context.toast("Label already exists!")
                }
                else -> {
                    repository.addLabel(Label(label = text))
                    binding.labelCancelBtn.performClick()
                }
            }
        }
}
