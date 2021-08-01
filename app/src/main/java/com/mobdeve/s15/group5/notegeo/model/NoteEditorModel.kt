package com.mobdeve.s15.group5.notegeo.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NoteEditorModel: ViewModel() {
    val selectedBackgroundColor = MutableLiveData<Int>()

    fun select(backgroundColor: Int) {
        selectedBackgroundColor.value = backgroundColor
    }
}