package com.mobdeve.s15.group5.notegeo.label

import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import com.mobdeve.s15.group5.notegeo.focusAndOpenKeyboard

@BindingAdapter("setIsEditing", "showOnEdit")
fun ImageButton.setEditingVisibility(isEditing: ObservableBoolean, showOnEdit: Boolean) {
    visibility = if (isEditing.get() xor showOnEdit) View.GONE else View.VISIBLE
}

@BindingAdapter("setIsEditing")
fun EditText.setEditable(isEditing: ObservableBoolean) {
    if (isEditing.get()) {
        inputType = InputType.TYPE_CLASS_TEXT
        isFocusableInTouchMode = true
        requestFocus()
        setSelection(text.length)
        (context as AppCompatActivity).focusAndOpenKeyboard(this)
    } else {
        inputType = InputType.TYPE_NULL
        isFocusableInTouchMode = false
        clearFocus()
    }
}