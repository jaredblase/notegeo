package com.mobdeve.s15.group5.notegeo.editor

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobdeve.s15.group5.notegeo.models.Note
import android.text.format.DateFormat
import com.mobdeve.s15.group5.notegeo.databinding.ActivityEditNoteBinding
import com.mobdeve.s15.group5.notegeo.models.NoteAndLabel
import com.mobdeve.s15.group5.notegeo.toast
import java.util.Date

class NoteEditorViewModel: ViewModel() {
    var noteAndLabel = NoteAndLabel()
        set(value) {
            field = value
            setBgColor(field.note.color)
            updateNoteEditDate()
            mPinned.value = field.note.isPinned
            labelName.value = field.label?.label
        }
    val selectedBackgroundColor = MutableLiveData<Int>()
    val dateEdited = MutableLiveData<String>()
    var mPinned = MutableLiveData<Boolean>()
    val isEditing = MutableLiveData<Boolean>()
    val labelName = MutableLiveData<String?>()

    fun setBgColor(backgroundColor: Int) {
        selectedBackgroundColor.value = backgroundColor
    }

    fun togglePin() {
        mPinned.value = mPinned.value?.not() ?: false
    }

    /**
     * update note's dateEdited attribute while also setting the view's dateEdited textView
     */
    private fun updateNoteEditDate() {
        val date = Date()
        noteAndLabel.note.dateEdited = date
        dateEdited.value = "Edited ${DateFormat.format("dd MMM yy kk:mm", date)}"
    }

    fun assignLabel(id: Int, name: String?) {
        // a change is made
        if (id != noteAndLabel.note.label) {
            updateNoteEditDate()

            if (id == -1) {
                noteAndLabel.note.label = null
            } else {
                noteAndLabel.note.label = id
            }

            labelName.value = name
        }
    }

    /**
     * saves text edits, requires user confirmation since they are more critical/essential.
     */
    fun save(binding: ActivityEditNoteBinding) {
        val titleText = binding.editorTitleEt.text.toString().trim()
        val bodyText = binding.editorBodyEt.text.toString().trim()

        if (titleText.isNotEmpty() || titleText.isNotEmpty()) {
            with(noteAndLabel.note) {
                title = titleText
                body = bodyText
            }

            updateNoteEditDate()
            isEditing.value = false
            binding.root.context.toast("Saved!")
        } else {
            binding.root.context.toast("Cannot save blank note!")
        }
    }

    /**
     * other attribute aside from text are saved here.
     */
    fun finalSave() {
        with(noteAndLabel.note) {
            color = selectedBackgroundColor.value ?: Note.DEFAULT_COLOR
            isPinned = mPinned.value ?: false
            // TODO: Location and alarm here
        }
    }
}