package com.mobdeve.s15.group5.notegeo.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mobdeve.s15.group5.notegeo.NoteGeoApplication
import com.mobdeve.s15.group5.notegeo.editor.EditNoteActivity
import com.mobdeve.s15.group5.notegeo.label.LabelActivity
import com.mobdeve.s15.group5.notegeo.R
import com.mobdeve.s15.group5.notegeo.buildConfirmationDialog
import com.mobdeve.s15.group5.notegeo.recyclebin.RecycleBinActivity
import com.mobdeve.s15.group5.notegeo.databinding.ActivityMainBinding
import com.mobdeve.s15.group5.notegeo.models.NoteAndLabel
import com.mobdeve.s15.group5.notegeo.models.ViewModelFactory
import com.mobdeve.s15.group5.notegeo.noteview.ItemOffsetDecoration
import com.mobdeve.s15.group5.notegeo.noteview.NoteAdapter
import com.mobdeve.s15.group5.notegeo.noteview.NoteDetailsLookup
import com.mobdeve.s15.group5.notegeo.noteview.NoteKeyProvider
import com.mobdeve.s15.group5.notegeo.toast
import kotlinx.coroutines.Dispatchers
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val model by viewModels<HomeViewModel> {
        ViewModelFactory(
            (application as NoteGeoApplication).repo,
            Dispatchers.Default
        )
    }
    private val adapter = NoteAdapter { launchEditor(it) }
    private val smoothScroller by lazy {
        object : LinearSmoothScroller(this) {
            override fun getVerticalSnapPreference() = SNAP_TO_ANY
        }
    }
    private val editorResultLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
            // a note was passed
            result.data?.getParcelableExtra<NoteAndLabel>(EditNoteActivity.NOTE_AND_LABEL)?.let {
                when (result.resultCode) {
                    RESULT_OK -> model.upsertNote(it.note)

                    EditNoteActivity.DELETE -> model.recycleNote(it.note._id)

                    // save og then open another editor
                    EditNoteActivity.DUPLICATE -> {
                        model.upsertNote(it.note)
                        launchEditor(
                            NoteAndLabel(it.note.copy(_id = 0, dateEdited = Date()), it.label)
                        )
                    }

                    else -> this.toast("Blank note deleted!")
                }
            }
        }

    private fun launchEditor(note: NoteAndLabel? = null) {
        editorResultLauncher.launch(Intent(this, EditNoteActivity::class.java).apply {
            note?.let {
                putExtra(EditNoteActivity.NOTE_AND_LABEL, it)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get data from db
        model.savedNotes.observeForever {
            binding.progressIndicator.visibility = View.GONE
            model.filterNotes(adapter, binding.homeSv.query)
            binding.emptyIv.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }

        // setup layout managers
        val gridLayout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
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

        model.postPosition.observe(this) {
            if (model.lastSavedNote?.isPinned == true) {
                smoothScroller.targetPosition = it // assign the position obtained
                binding.notesListRv.layoutManager?.startSmoothScroll(smoothScroller)
            }
        }

        model.filterReminders.observe(this) { model.filterNotes(adapter, binding.homeSv.query) }

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
                R.id.notes_menu_btn -> model.filterReminders.value = false
                R.id.reminders_menu_btn -> model.filterReminders.value = true
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

        with(binding) {
            homeSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = false

                override fun onQueryTextChange(newText: String?): Boolean {
                    model.filterNotes(adapter, newText)
                    return true
                }
            })

            // set click listeners
            homeDeleteBtn.setOnClickListener {
                buildConfirmationDialog(getString(R.string.confirm_selection_delete)) { _, _ ->
                    model.recycleNotes(adapter.tracker?.selection?.toList(), applicationContext)
                }.show()
            }
            sideMenuBtn.setOnClickListener { mainDl.open() }
            changeLayoutBtn.setOnClickListener { model.toggleView() }
            addNoteFab.setOnClickListener { launchEditor() }
        }
    }

    override fun onBackPressed() {
        when {
            adapter.tracker?.hasSelection() == true -> adapter.tracker?.clearSelection()
            binding.mainDl.isDrawerOpen(GravityCompat.START) -> binding.mainDl.closeDrawers()
            else -> super.onBackPressed()
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