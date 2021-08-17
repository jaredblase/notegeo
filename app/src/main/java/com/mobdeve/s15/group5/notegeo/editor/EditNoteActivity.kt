package com.mobdeve.s15.group5.notegeo.editor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.mobdeve.s15.group5.notegeo.*
import com.mobdeve.s15.group5.notegeo.databinding.ActivityEditNoteBinding
import com.mobdeve.s15.group5.notegeo.home.MainActivity
import com.mobdeve.s15.group5.notegeo.models.NoteAndLabel
import com.mobdeve.s15.group5.notegeo.models.ViewModelFactory

class EditNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditNoteBinding
    private val model by viewModels<NoteEditorViewModel> { ViewModelFactory((application as NoteGeoApplication).repo) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set note to view model, if no note was passed, create a new note
        model.noteAndLabel = intent.getParcelableExtra(NOTE_AND_LABEL) ?: NoteAndLabel()

        // bind model with layout using observers
        model.selectedBackgroundColor.observe(this) { binding.root.setBackgroundColor(it) }
        model.dateEdited.observe(this) { binding.editorDateEditedTv.text = it }
        model.mPinned.observe(this) {
            binding.setPinnedBtn.setImageResource(if (it) R.drawable.ic_pin_filled else R.drawable.ic_pin)
        }

        // hide other buttons when editing
        model.isEditing.observe(this) {
            val value: Int
            val antiValue: Int

            if (it) {
                value = View.GONE
                antiValue = View.VISIBLE
            } else {
                value = View.VISIBLE
                antiValue = View.GONE
            }

            with(binding) {
                setPinnedBtn.visibility = value
                setAlarmBtn.visibility = value
                setLocationBtn.visibility = value
                editorMoreOptions.visibility = value
                editorSaveBtn.visibility = antiValue
                editorCancelBtn.visibility = antiValue
            }
        }

        // setup label watcher
        model.labelName.observe(this) {
            with(binding.labelTv) {
                text = it
                visibility = if (it == null) View.GONE else View.VISIBLE
            }
        }

        // TODO: setup alarm listener
        binding.alarmTv.visibility = View.GONE

        // TODO: setup location listener
        binding.locationTv.visibility = View.GONE

        val watcher = MyWatcher { model.isEditing.value = true }

        // note text editing
        refreshFields()
        binding.editorTitleEt.addTextChangedListener(watcher)
        binding.editorBodyEt.addTextChangedListener(watcher)

        binding.editorMoreOptions.setOnClickListener {
            // assures only menu will appear even with multiple clicks
            if (supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) == null) {
                EditorMenuFragment().show(supportFragmentManager, FRAGMENT_TAG)
            }
        }

        with(binding) {
            editorSaveBtn.setOnClickListener { model.save(this); clearFocus(it) }
            editorCancelBtn.setOnClickListener { refreshFields(); clearFocus(it) }
            setPinnedBtn.setOnClickListener { model.togglePin() }
        }
    }

    private fun clearFocus(v: View) {
        hideKeyboard(v)
        binding.editorBodyEt.clearFocus()
        binding.editorTitleEt.clearFocus()
    }

    private fun refreshFields() {
        with(binding) {
            with(model.noteAndLabel.note) {
                editorTitleEt.setText(title)
                editorBodyEt.setText(body)
            }
        }
        model.isEditing.value = false
    }

    override fun onBackPressed() {
        if (model.isEditing.value == true) {
            refreshFields()
        } else if (!model.noteAndLabel.note.isBlank) {
            model.finalSave()
            setResult(RESULT_OK, Intent(this, MainActivity::class.java).apply {
                putExtra(NOTE_AND_LABEL, model.noteAndLabel)
            })
            finish()
        } else {
            toast("Blank note deleted!")
            super.onBackPressed()
        }
    }

    companion object {
        private const val FRAGMENT_TAG = "Editor Menu"
        const val NOTE_AND_LABEL = "NoteAndLabel"
        const val DELETE = 100
        const val DUPLICATE = 200
    }
}