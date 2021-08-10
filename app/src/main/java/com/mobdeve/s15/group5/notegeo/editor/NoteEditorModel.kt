package com.mobdeve.s15.group5.notegeo.editor

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobdeve.s15.group5.notegeo.models.Note
import android.text.format.DateFormat
import com.mobdeve.s15.group5.notegeo.databinding.ActivityEditNoteBinding
import com.mobdeve.s15.group5.notegeo.toast
import java.util.Date

class NoteEditorModel: ViewModel() {
    var note = Note()
        set(value) {
            field = value
            setBgColor(field.color)
            setFormattedDate()
            mPinned.value = field.isPinned
        }
    val selectedBackgroundColor = MutableLiveData<Int>()
    val dateEdited = MutableLiveData<String>()
    var wasEdited = false
        private set
    var mPinned = MutableLiveData<Boolean>()

    fun setBgColor(backgroundColor: Int) {
        selectedBackgroundColor.value = backgroundColor
    }

    fun togglePin() {
        mPinned.value = mPinned.value?.not() ?: false
    }

    private fun setFormattedDate() {
        val date = DateFormat.format("dd MMM yy kk:mm", note.dateEdited).toString()
        dateEdited.value = "Edited $date"
    }

    fun save(binding: ActivityEditNoteBinding) {
        with(note) {
            title = binding.editorTitleEt.text.toString()
            body = binding.editorBodyEt.text.toString()
            color = selectedBackgroundColor.value ?: Note.DEFAULT_COLOR
            dateEdited = Date()
            isPinned = mPinned.value ?: false
        }
        setFormattedDate()
        wasEdited = true
        binding.root.context.toast("Saved!")
    }
}