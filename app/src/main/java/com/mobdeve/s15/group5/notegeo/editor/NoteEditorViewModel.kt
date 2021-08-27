package com.mobdeve.s15.group5.notegeo.editor

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobdeve.s15.group5.notegeo.models.Note
import android.text.format.DateFormat
import com.mobdeve.s15.group5.notegeo.alarms.AlarmReceiver
import com.mobdeve.s15.group5.notegeo.databinding.ActivityEditNoteBinding
import com.mobdeve.s15.group5.notegeo.models.Label
import com.mobdeve.s15.group5.notegeo.models.NoteAndLabel
import com.mobdeve.s15.group5.notegeo.toast
import java.util.Date

class NoteEditorViewModel: ViewModel() {
    var noteAndLabel = NoteAndLabel()
        set(value) {
            field = value
            setBgColor(field.note.color)
            setDateEditedText()
            setDateAlarm(field.note.dateAlarm)
            mPinned.value = field.note.isPinned
            labelName.value = field.label?.name
        }
    val selectedBackgroundColor = MutableLiveData<Int>()
    val dateEdited = MutableLiveData<String>()
    val dateAlarm = MutableLiveData<String?>()
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
    private fun setDateEditedText(toUpdate: Boolean = false) {
        if (toUpdate) {
            noteAndLabel.note.dateEdited = Date()
        }
        dateEdited.value = "Edited ${DateFormat.format("dd MMM yy kk:mm", noteAndLabel.note.dateEdited)}"
    }

    fun setDateAlarm(date: Date?) {
        if (date != noteAndLabel.note.dateAlarm) {
            setDateEditedText(true)
            noteAndLabel.note.dateAlarm = date
        }

        if (date != null) {
            dateAlarm.value = "${DateFormat.format("dd MMM yy kk:mm", noteAndLabel.note.dateAlarm)}"
        } else {
            dateAlarm.value = null
        }
    }

    fun assignLabel(label: Label?) {
        // a change is made
        if (label?._id != noteAndLabel.note.label) {
            setDateEditedText(true)
        }
        noteAndLabel.note.label = label?._id
        labelName.value = label?.name
    }

    /**
     * saves text edits, requires user confirmation since they are more critical/essential.
     */
    fun save(binding: ActivityEditNoteBinding): Boolean {
        val titleText = binding.editorTitleEt.text.toString().trim()
        val bodyText = binding.editorBodyEt.text.toString().trim()

        return if (titleText.isNotEmpty() || bodyText.isNotEmpty()) {
            with(noteAndLabel.note) {
                title = titleText
                body = bodyText
            }

            setDateEditedText(true)
            isEditing.value = false
            binding.root.context.toast("Saved!")
            true
        } else {
            binding.root.context.toast("Cannot save blank note!")
            false
        }
    }

    /**
     * other attribute aside from text are saved here.
     */
    fun finalSave(context: Context) {
        with(noteAndLabel.note) {
            color = selectedBackgroundColor.value ?: Note.DEFAULT_COLOR
            isPinned = mPinned.value ?: false
            // TODO: Location and alarm here
            val alarmReceiver = AlarmReceiver()
            alarmReceiver.cancelAlarm(context, noteAndLabel.note)
            if (noteAndLabel.note.dateAlarm != null && noteAndLabel.note.dateAlarm!!.after(Date())) {
                alarmReceiver.setAlarm(context, noteAndLabel.note)
            }
        }
    }
}