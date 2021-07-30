package com.mobdeve.s15.group5.notegeo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mobdeve.s15.group5.notegeo.databinding.FragmentEditorMenuBinding

class EditorMenuFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentEditorMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditorMenuBinding.inflate(inflater, container, false)

        binding.navigationView.setNavigationItemSelectedListener {
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

        return binding.root
    }
}