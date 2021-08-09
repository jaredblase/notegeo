package com.mobdeve.s15.group5.notegeo.recyclebin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mobdeve.s15.group5.notegeo.NoteGeoApplication
import com.mobdeve.s15.group5.notegeo.R
import com.mobdeve.s15.group5.notegeo.databinding.ActivityRecycleBinBinding
import com.mobdeve.s15.group5.notegeo.noteview.*
import com.mobdeve.s15.group5.notegeo.models.ViewModelFactory
import com.mobdeve.s15.group5.notegeo.toast

class RecycleBinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecycleBinBinding
    private val model by viewModels<RecycleBinViewModel> { ViewModelFactory((application as NoteGeoApplication).repo) }
    private val adapter = NoteAdapter { this.toast("NOTE SELECTED") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecycleBinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // everytime the list is updated
        model.deletedNotes.observe(this) {
            adapter.submitList(it)

            with(binding) {
                if (it.isEmpty()) {
                    emptyIv.visibility = View.VISIBLE
                    recycleBinMnuBtn.visibility = View.GONE
                } else {
                    emptyIv.visibility = View.GONE
                    recycleBinMnuBtn.visibility = View.VISIBLE
                }
            }
        }

        // setup recycler view
        binding.recycleBinRv.let {
            it.adapter = adapter
            it.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
                    gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
                }
            it.addItemDecoration(ItemOffsetDecoration(this, R.dimen.notes_offset))
        }

        val tracker = SelectionTracker.Builder(
            "mySelection",
            binding.recycleBinRv,
            NoteKeyProvider(adapter),
            NoteDetailsLookup(binding.recycleBinRv),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        // setup popup menu
        val popup = PopupMenu(this, binding.recycleBinMnuBtn)
        menuInflater.inflate(R.menu.recycle_bin_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.rb_delete_selected_btn -> model.delete(adapter.tracker?.selection?.toList())
                R.id.rb_restore_selected_btn -> model.restore(adapter.tracker?.selection?.toList())
                R.id.delete_all_btn -> model.deleteAll()
                R.id.restore_all_btn -> model.restoreAll()
            }
            tracker.clearSelection()
            true
        }

        adapter.tracker = tracker.apply {
            addObserver(object: SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()

                    with(popup.menu) {
                        val value = !selection.isEmpty
                        findItem(R.id.rb_delete_selected_btn).isVisible = value
                        findItem(R.id.rb_restore_selected_btn).isVisible = value
                    }
                }
            })
        }

        binding.recycleBinMnuBtn.setOnClickListener { popup.show() }
    }

    override fun onBackPressed() {
        if (adapter.tracker?.hasSelection() == true) {
            adapter.tracker?.clearSelection()
        } else {
            super.onBackPressed()
        }
    }
}