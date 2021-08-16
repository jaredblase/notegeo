package com.mobdeve.s15.group5.notegeo.editor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mobdeve.s15.group5.notegeo.R
import com.mobdeve.s15.group5.notegeo.databinding.FragmentEditorMenuBinding
import com.mobdeve.s15.group5.notegeo.home.MainActivity
import com.mobdeve.s15.group5.notegeo.label.LabelActivity
import com.mobdeve.s15.group5.notegeo.toast

class EditorMenuFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentEditorMenuBinding
    private val model by activityViewModels<NoteEditorViewModel>()
    private val selectLabelLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        // A label was selected
        result.data?.let {
            val id = it.getIntExtra(LabelActivity.LABEL_ID, -1)
            val name = it.getStringExtra(LabelActivity.LABEL_NAME)
            model.assignLabel(id, name)
        }
        dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditorMenuBinding.inflate(inflater, container, false)

        binding.editorMnu.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.editor_delete_btn -> {
                    activity?.run {
                        setResult(EditNoteActivity.DELETE, Intent(context, MainActivity::class.java).apply {
                            putExtra(EditNoteActivity.NOTE_AND_LABEL, model.noteAndLabel)
                        })
                        finish()
                    }
                    dismiss()
                    true
                }

                R.id.editor_copy_btn -> {
                    if (!model.noteAndLabel.note.isBlank) {
                        activity?.run {
                            setResult(EditNoteActivity.DUPLICATE, Intent(context, MainActivity::class.java).apply {
                                putExtra(EditNoteActivity.NOTE_AND_LABEL, model.noteAndLabel)
                            })
                            finish()
                        }
                    } else {
                        context?.toast("Cannot duplicate blank note!")
                    }
                    dismiss()
                    true
                }

                R.id.editor_labels_btn -> {
                    selectLabelLauncher.launch(Intent(context, LabelActivity::class.java).apply {
                        putExtra(LabelActivity.IS_SELECTING, true)
                        putExtra(LabelActivity.BG_COLOR, model.selectedBackgroundColor.value)
                        model.noteAndLabel.note.label?.let { labelId ->
                            putExtra(LabelActivity.LABEL_ID, labelId)
                        }
                    })
                    true
                }

                else -> false
            }
        }

        // color palette configuration
        val colorSelectionView = binding.editorMnu.getHeaderView(0) as ViewGroup
        colorSelectionView.children.forEach { paletteHolder ->
            paletteHolder as PaletteHolder
            paletteHolder.isSelected = paletteHolder.backgroundColor == model.selectedBackgroundColor.value

            paletteHolder.setOnClickListener {
                it as PaletteHolder
                model.setBgColor(it.backgroundColor)
            }
        }

        model.selectedBackgroundColor.observe(this) { backgroundColor ->
            colorSelectionView.children.forEach {
                it as PaletteHolder
                it.isPaletteSelected = it.backgroundColor == backgroundColor
            }
        }

        return binding.root
    }
}