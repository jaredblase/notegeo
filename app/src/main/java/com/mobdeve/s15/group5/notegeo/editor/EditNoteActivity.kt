package com.mobdeve.s15.group5.notegeo.editor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.mobdeve.s15.group5.notegeo.databinding.ActivityEditNoteBinding
import com.mobdeve.s15.group5.notegeo.models.Note

class EditNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditNoteBinding
    private val model: NoteEditorModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set note to view model
        intent.getParcelableExtra<Note>(NOTE)?.let { model.note = it }

        // bind model with layout using observers
        model.selectedBackgroundColor.observe(this) { binding.root.setBackgroundColor(it) }
        model.dateEdited.observe(this) { binding.editorDateEditedTv.text = it }

        with(binding) {
            with(model.note) {
                editorTitleEt.setText(title)
                editorBodyEt.setText(body)
            }
        }

        binding.editorMoreOptions.setOnClickListener {
            // assures only menu will appear even with multiple clicks
            if (supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) == null) {
                EditorMenuFragment().show(supportFragmentManager, FRAGMENT_TAG)
            }
        }
    }

    companion object {
        private const val FRAGMENT_TAG = "Editor Menu"
        const val NOTE = "Note"
    }
}