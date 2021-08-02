package com.mobdeve.s15.group5.notegeo.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mobdeve.s15.group5.notegeo.DataHelper
import com.mobdeve.s15.group5.notegeo.editor.EditNoteActivity
import com.mobdeve.s15.group5.notegeo.label.LabelActivity
import com.mobdeve.s15.group5.notegeo.R
import com.mobdeve.s15.group5.notegeo.recyclebin.RecycleBinActivity
import com.mobdeve.s15.group5.notegeo.databinding.ActivityMainBinding
import com.mobdeve.s15.group5.notegeo.models.Note

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val data = ObservableArrayList<Note>()
    private val isGridView = ObservableBoolean(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Get data from db
        data.addAll(DataHelper.loadNotes())

        binding.sideMenuBtn.setOnClickListener { binding.mainDl.open() }

        // setup recycler view
        binding.notesListRv.apply {
            adapter = NoteAdapter(data)
            addItemDecoration(ItemOffsetDecoration(this.context, R.dimen.notes_offset))
        }


        binding.changeLayoutBtn.setOnClickListener { isGridView.set(!isGridView.get()) }

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

        binding.addNoteFab.setOnClickListener {
            startActivity(Intent(this, EditNoteActivity::class.java))
        }

        // data binding
        binding.notes = data
        binding.gridLayout = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        }
        binding.linearLayout = LinearLayoutManager(this)
        binding.isGrid = isGridView

        binding.executePendingBindings()
    }
}