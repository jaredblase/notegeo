package com.mobdeve.s15.group5.notegeo.label

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobdeve.s15.group5.notegeo.NoteGeoApplication
import com.mobdeve.s15.group5.notegeo.R
import com.mobdeve.s15.group5.notegeo.databinding.ActivityLabelBinding
import com.mobdeve.s15.group5.notegeo.models.ViewModelFactory

class LabelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLabelBinding
    private lateinit var popup: PopupMenu
    private val model by viewModels<LabelViewModel> { ViewModelFactory((application as NoteGeoApplication).repo) }

    private val rv
        get() = binding.labelsRv

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLabelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get configuration from extras
        val isSelecting = intent.getBooleanExtra(IS_SELECTING, false)

        // setup recycler view
        val adapter = LabelAdapter()
        val layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
        rv.layoutManager = layoutManager

        // load data from db once
        model.allLabels.observe(this) {
            adapter.submitList(it)
            model.listIsEmpty.set(it.isEmpty())
            model.allLabels.removeObservers(this)
        }


        if (isSelecting) {
            // hide unnecessary buttons
            (binding.labelsMnuBtn.parent as ViewGroup).removeView(binding.labelsMnuBtn)
            binding.saveBtn.visibility = View.GONE
        } else {
            // setup popup menu
            popup = PopupMenu(this, binding.labelsMnuBtn).apply {
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.delete_selected_label_btn -> model.deleteSelectedLabels(adapter)
                        R.id.delete_all_label_btn -> model.removeAllLabels(adapter)
                    }
                    true
                }
            }.also { menuInflater.inflate(R.menu.labels_menu, it.menu) }
            binding.labelsMnuBtn.setOnClickListener { popup.show() }

            // saving functionality
            binding.saveBtn.setOnClickListener { model.updateLabels(this) }
        }

        // add label function
        binding.addLabelLl.setOnClickListener { model.addLabel(adapter) }

        binding.isEmpty = model.listIsEmpty
        binding.executePendingBindings()
    }

    companion object {
        const val IS_SELECTING = "IS_SELECTING"
        const val LABEL_ID = "LABEL_ID"
        const val LABEL_NAME = "LABEL_NAME"
    }
}