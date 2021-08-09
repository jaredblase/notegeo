package com.mobdeve.s15.group5.notegeo.editor

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobdeve.s15.group5.notegeo.models.Note
import android.text.format.DateFormat

class NoteEditorModel: ViewModel() {
    var note = Note()
        set(value) {
            field = value
            setBgColor(field.color)
            setFormattedDate()
        }
    val selectedBackgroundColor = MutableLiveData<Int>()
    val dateEdited = MutableLiveData<String>()

    fun setBgColor(backgroundColor: Int) {
        selectedBackgroundColor.value = backgroundColor
    }

    private fun setFormattedDate() {
        val date = DateFormat.format("dd MMM yy kk:mm", note.dateEdited).toString()
        dateEdited.value = "Edited $date"
    }
}