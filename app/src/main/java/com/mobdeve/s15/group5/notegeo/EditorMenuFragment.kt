package com.mobdeve.s15.group5.notegeo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mobdeve.s15.group5.notegeo.databinding.FragmentEditorMenuBinding
import com.mobdeve.s15.group5.notegeo.views.PaletteHolder

class EditorMenuFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentEditorMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditorMenuBinding.inflate(inflater, container, false)

        binding.editorMnu.setNavigationItemSelectedListener {
            val result = when (it.itemId) {
                R.id.editor_delete_btn -> {
                    // TODO: Delete note
                    Toast.makeText(activity?.applicationContext, "Deleted!", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.editor_copy_btn -> {
                    // TODO: Duplicate note
                    Toast.makeText(activity?.applicationContext, "Copied!", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.editor_labels_btn -> {
                    // TODO: Show labels
                    Toast.makeText(activity?.applicationContext, "Showing labels...", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }

            dismiss()
            result
        }

        val colorSelectionView = binding.editorMnu.getHeaderView(0) as ViewGroup

        colorSelectionView.children.forEach { paletteHolder ->
            paletteHolder.setOnClickListener {
                it as PaletteHolder
                it.isPaletteSelected.value = true
            }
        }

        return binding.root
    }
}