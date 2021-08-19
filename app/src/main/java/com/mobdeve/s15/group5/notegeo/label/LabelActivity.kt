package com.mobdeve.s15.group5.notegeo.label

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobdeve.s15.group5.notegeo.*
import com.mobdeve.s15.group5.notegeo.databinding.ActivityLabelBinding
import com.mobdeve.s15.group5.notegeo.editor.EditorMenuFragment
import com.mobdeve.s15.group5.notegeo.models.ViewModelFactory
import kotlin.properties.Delegates

class LabelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLabelBinding
    private lateinit var popup: PopupMenu
    private var isSelecting by Delegates.notNull<Boolean>()
    private val model by viewModels<LabelViewModel> { ViewModelFactory((application as NoteGeoApplication).repo) }
    private lateinit var adapter: LabelAdapter

    private val rv
        get() = binding.labelsRv

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLabelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get configuration from extras
        isSelecting = intent.getBooleanExtra(IS_SELECTING, false)
        val labelId = intent.getIntExtra(LABEL_ID, -1)
        val bgColor = intent.getIntExtra(BG_COLOR, -1)

        // setup recycler view
        adapter = if (isSelecting) ChooseLabelAdapter() else LabelAdapter()
        val layoutManager = LinearLayoutManager(this)
        rv.layoutManager = layoutManager
        rv.adapter = adapter

        model.repoLabels.observe(this) { list ->
            model.allLabels.value = list
            model.repoLabels.removeObservers(this)
        }

        model.allLabels.observe(this) {
            adapter.submitList(it)
            binding.progressIndicator.visibility = View.GONE

            // if there is a selected label, set selected properties
            if (labelId != -1) {
                for ((i, label) in adapter.currentList.withIndex()) {
                    if (label._id == labelId) {
                        label.isChecked.set(true)
                        (adapter as ChooseLabelAdapter).lastSelectedPosition = i
                    }
                }
            }
            binding.emptyIv.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }


        if (isSelecting) {
            window.decorView.setBackgroundColor(bgColor)
            // hide unnecessary buttons
            (binding.labelsMnuBtn.parent as ViewGroup).removeView(binding.labelsMnuBtn)
        } else {
            // setup popup menu
            popup = PopupMenu(this, binding.labelsMnuBtn).apply {
                setOnMenuItemClickListener { item ->
                    if (item.itemId == R.id.delete_selected_label_btn) {
                        model.deleteSelectedLabels()
                    }
                    true
                }
            }.also { menuInflater.inflate(R.menu.labels_menu, it.menu) }
            binding.labelsMnuBtn.setOnClickListener { popup.show() }
        }

        model.numSelected.observe(this) {
            binding.labelsMnuBtn.visibility = if (it == 0) View.GONE else View.VISIBLE
        }

        // add label stuff
        binding.addLabelCl.setOnClickListener { focusAndOpenKeyboard(binding.addLabelEt) }
        binding.addLabelEt.addTextChangedListener(MyWatcher {
            val value = if (it?.length == 0) View.GONE else View.VISIBLE
            binding.labelAddBtn.visibility = value
            binding.labelCancelBtn.visibility = value
        })

        val context = this
        with(binding) {
            labelAddBtn.setOnClickListener { model.addLabel(this, context) }
            labelCancelBtn.setOnClickListener {
                addLabelEt.setText("")
                addLabelEt.clearFocus()
                hideKeyboard(it)
            }
        }
    }

    override fun onBackPressed() {
        when {
            // is still editing an entry
            adapter.lastEditedPosition != -1 -> {
                binding.labelsRv.layoutManager?.findViewByPosition(adapter.lastEditedPosition)
                    ?.findViewById<ImageButton>(R.id.cancel_btn)?.performClick()
            }
            isSelecting -> {
                val pos = (adapter as ChooseLabelAdapter).lastSelectedPosition
                val item = if (pos == -1) null else adapter.currentList[pos]

                setResult(RESULT_OK, Intent(this, EditorMenuFragment::class.java).apply {
                    putExtra(LABEL, item)
                })
                finish()
            }
            else -> super.onBackPressed()
        }
    }

    companion object {
        const val IS_SELECTING = "IS_SELECTING"
        const val LABEL_ID = "LABEL_ID"
        const val LABEL = "LABEL"
        const val BG_COLOR = "BACKGROUND_COLOR"
    }
}