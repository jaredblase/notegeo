package com.mobdeve.s15.group5.notegeo.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NoteEditorModel: ViewModel() {
    val selected = MutableLiveData<Int>()

    fun select(styleId: Int) {
        selected.value = styleId
    }
}