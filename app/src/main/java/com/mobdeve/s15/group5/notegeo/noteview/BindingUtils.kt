package com.mobdeve.s15.group5.notegeo.noteview

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.mobdeve.s15.group5.notegeo.models.Note

@BindingAdapter("noteColor")
fun ConstraintLayout.setBackgroundColor(item: Note) {
    setBackgroundColor(item.color)
}

@BindingAdapter("noteTitle")
fun TextView.setNoteTitle(item: Note) {
    visibility = if (item.title.isEmpty()) View.GONE else View.VISIBLE
    text = item.title
}

@BindingAdapter("noteBody")
fun TextView.setNoteBody(item: Note) {
    visibility = if (item.body.isEmpty()) View.GONE else View.VISIBLE
    setPadding(paddingLeft, if (item.title.isEmpty()) 45 else 10, paddingRight, paddingBottom)
    text = item.body
}

@BindingAdapter("pinned")
fun ImageView.setPinned(item: Note) {
    visibility = if (item.isPinned) View.VISIBLE else View.GONE
}