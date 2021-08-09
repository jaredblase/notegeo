package com.mobdeve.s15.group5.notegeo.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mobdeve.s15.group5.notegeo.NoteGeoApplication
import com.mobdeve.s15.group5.notegeo.editor.EditNoteActivity
import com.mobdeve.s15.group5.notegeo.label.LabelActivity
import com.mobdeve.s15.group5.notegeo.R
import com.mobdeve.s15.group5.notegeo.recyclebin.RecycleBinActivity
import com.mobdeve.s15.group5.notegeo.databinding.ActivityMainBinding
import com.mobdeve.s15.group5.notegeo.models.ViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val model by viewModels<HomeViewModel> { ViewModelFactory((application as NoteGeoApplication).repo) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = NoteAdapter()

        // get data from db once
        model.savedNotes.observe(this) {
            adapter.submitList(it)
            model.isListEmpty.set(it.isEmpty())
            model.savedNotes.removeObservers(this)
        }

        // setup recycler view
        binding.notesListRv.apply {
            this.adapter = adapter
            addItemDecoration(ItemOffsetDecoration(this.context, R.dimen.notes_offset))
        }

        // setup menu
        binding.drawerMenu.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.notes_menu_btn -> {
                    // TODO: Remove any filters
                }

                R.id.reminders_menu_btn -> {
                    // TODO: Filter to only be notes with reminders
                }

                R.id.deleted_menu_btn -> {
                    startActivity(Intent(this, RecycleBinActivity::class.java))
                }

                R.id.labels_menu_btn -> {
                    startActivity(Intent(this, LabelActivity::class.java))
                }
            }

            binding.mainDl.closeDrawers()
            true
        }

        val context = this
        with(binding) {
            // set click listeners
            sideMenuBtn.setOnClickListener { mainDl.open() }
            changeLayoutBtn.setOnClickListener { context.model.toggleView() }
            addNoteFab.setOnClickListener {
                startActivity(Intent(context, EditNoteActivity::class.java))
            }

            // data binding
            model = context.model
            gridLayout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
                gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
            }
            linearLayout = LinearLayoutManager(context)

            executePendingBindings()
        }
    }

    override fun onResume() {
        super.onResume()

        with(PreferenceManager.getDefaultSharedPreferences(this)) {
            model.isGridView.set(getBoolean(IS_GRID_VIEW, true))
        }
    }

    override fun onStop() {
        super.onStop()

        with(PreferenceManager.getDefaultSharedPreferences(this).edit()) {
            putBoolean(IS_GRID_VIEW, model.isGridView.get())
            apply()
        }
    }

    companion object {
        private const val IS_GRID_VIEW = "IS GRID VIEW"
    }
}