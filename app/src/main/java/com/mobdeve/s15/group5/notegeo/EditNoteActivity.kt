package com.mobdeve.s15.group5.notegeo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobdeve.s15.group5.notegeo.databinding.ActivityEditNoteBinding

class EditNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editorMoreOptions.setOnClickListener {
            // assures only menu will appear even with multiple clicks
            if (supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) == null) {
                EditorMenuFragment().show(supportFragmentManager, FRAGMENT_TAG)
            }
        }
    }

    companion object {
        private const val FRAGMENT_TAG = "Editor Menu"
    }
}