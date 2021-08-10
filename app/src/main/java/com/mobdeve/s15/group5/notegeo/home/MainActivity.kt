package com.mobdeve.s15.group5.notegeo.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mobdeve.s15.group5.notegeo.NoteGeoApplication
import com.mobdeve.s15.group5.notegeo.editor.EditNoteActivity
import com.mobdeve.s15.group5.notegeo.label.LabelActivity
import com.mobdeve.s15.group5.notegeo.R
import com.mobdeve.s15.group5.notegeo.recyclebin.RecycleBinActivity
import com.mobdeve.s15.group5.notegeo.databinding.ActivityMainBinding
import com.mobdeve.s15.group5.notegeo.models.Note
import com.mobdeve.s15.group5.notegeo.models.ViewModelFactory
import com.mobdeve.s15.group5.notegeo.noteview.ItemOffsetDecoration
import com.mobdeve.s15.group5.notegeo.noteview.NoteAdapter
import com.mobdeve.s15.group5.notegeo.noteview.NoteDetailsLookup
import com.mobdeve.s15.group5.notegeo.noteview.NoteKeyProvider

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val model by viewModels<HomeViewModel> { ViewModelFactory((application as NoteGeoApplication).repo) }
    private val adapter = NoteAdapter {
        // go to editor when a note is clicked
        editorResultLauncher.launch(Intent(this, EditNoteActivity::class.java).apply {
            putExtra(EditNoteActivity.NOTE, it)
        })
    }
    private val editorResultLauncher = registerForActivityResult(StartActivityForResult()) {
        // data was edited
        val note = it.data?.getParcelableExtra<Note>(EditNoteActivity.NOTE)

        when (it.resultCode) {
            RESULT_OK -> note?.let { model.upsertNote(note) }

            EditNoteActivity.DELETE -> note?.let { model.recycleNote(note._id) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get data from db
        model.savedNotes.observe(this) {
            adapter.submitList(it)
            binding.emptyIv.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }

        // setup layout managers
        val gridLayout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        }
        val linearLayout = LinearLayoutManager(this)

        model.isGridView.observe(this) {
            with(binding) {
                if (it) {
                    notesListRv.layoutManager = gridLayout
                    changeLayoutBtn.setImageResource(R.drawable.ic_tile_view)
                } else {
                    notesListRv.layoutManager = linearLayout
                    changeLayoutBtn.setImageResource(R.drawable.ic_card)
                }
            }
        }

        // setup recycler view
        binding.notesListRv.let {
            it.adapter = adapter
            it.addItemDecoration(ItemOffsetDecoration(this, R.dimen.notes_offset))
        }

        val tracker = SelectionTracker.Builder(
            "myHomeSelection",
            binding.notesListRv,
            NoteKeyProvider(adapter),
            NoteDetailsLookup(binding.notesListRv),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        adapter.tracker = tracker.apply {
            addObserver(object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    binding.homeDeleteBtn.visibility =
                        if (selection.isEmpty) View.GONE else View.VISIBLE
                }
            })
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
            homeDeleteBtn.setOnClickListener { model.recycleNotes(adapter.tracker?.selection?.toList()) }
            sideMenuBtn.setOnClickListener { mainDl.open() }
            changeLayoutBtn.setOnClickListener { model.toggleView() }
            addNoteFab.setOnClickListener {
                editorResultLauncher.launch(Intent(context, EditNoteActivity::class.java))
            }
        }
    }

    override fun onBackPressed() {
        if (adapter.tracker?.hasSelection() == true) {
            adapter.tracker?.clearSelection()
        } else {
            super.onBackPressed()
        }
    }

    /**
     * get saved layout style
     */
    override fun onResume() {
        super.onResume()

        with(PreferenceManager.getDefaultSharedPreferences(this)) {
            model.isGridView.value = getBoolean(IS_GRID_VIEW, true)
        }
    }

    /**
     * write preferred layout style
     */
    override fun onStop() {
        super.onStop()

        with(PreferenceManager.getDefaultSharedPreferences(this).edit()) {
            model.isGridView.value?.let {
                putBoolean(IS_GRID_VIEW, it)
                apply()
            }
        }
    }

    companion object {
        private const val IS_GRID_VIEW = "IS GRID VIEW"
    }
}