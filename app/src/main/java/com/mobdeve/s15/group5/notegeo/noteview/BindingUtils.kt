package com.mobdeve.s15.group5.notegeo.noteview

import android.graphics.Paint
import android.text.format.DateFormat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.google.android.flexbox.FlexboxLayout
import com.mobdeve.s15.group5.notegeo.models.NoteAndLabel

@BindingAdapter("noteColor")
fun ConstraintLayout.setBackgroundColor(item: NoteAndLabel) {
    setBackgroundColor(item.note.color)
}

@BindingAdapter("noteTitle")
fun TextView.setNoteTitle(item: NoteAndLabel) {
    visibility = if (item.note.title.isEmpty()) View.GONE else View.VISIBLE
    text = item.note.title
}

@BindingAdapter("noteBody")
fun TextView.setNoteBody(item: NoteAndLabel) {
    visibility = if (item.note.body.isEmpty()) View.GONE else View.VISIBLE
    setPadding(paddingLeft, if (item.note.title.isEmpty()) 45 else 10, paddingRight, paddingBottom)
    text = item.note.body
}

@BindingAdapter("pinned")
fun ImageView.setPinned(item: NoteAndLabel) {
    visibility = if (item.note.isPinned) View.VISIBLE else View.GONE
}

@BindingAdapter("bindItem")
fun FlexboxLayout.setItem(item: NoteAndLabel) {
    visibility = if (item.label == null && item.note.dateAlarm == null) View.GONE else View.VISIBLE
}

@BindingAdapter("noteLabel")
fun TextView.setLabel(item: NoteAndLabel) {
    if (item.label != null) {
        text = item.label!!.name
        visibility = View.VISIBLE
    } else {
        visibility = View.GONE
    }
}

@BindingAdapter("noteDateAlarm")
fun TextView.setDateAlarm(item: NoteAndLabel) {
    if (item.note.dateAlarm != null) {
        text = "${DateFormat.format("dd MMM yy kk:mm", item.note.dateAlarm)}"
        visibility = View.VISIBLE

        if (item.note.dateAlarm!!.time <= System.currentTimeMillis()) {
            paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }
    } else {
        visibility = View.GONE
    }
}
