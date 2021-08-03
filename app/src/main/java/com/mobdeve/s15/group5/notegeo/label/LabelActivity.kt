package com.mobdeve.s15.group5.notegeo.label

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.ObservableArrayList
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobdeve.s15.group5.notegeo.DataHelper
import com.mobdeve.s15.group5.notegeo.R
import com.mobdeve.s15.group5.notegeo.databinding.ActivityLabelBinding
import com.mobdeve.s15.group5.notegeo.models.LabelEntry

class LabelActivity : AppCompatActivity() {
    private val data = ObservableArrayList<LabelEntry>()
    private lateinit var binding: ActivityLabelBinding
    private lateinit var popup: PopupMenu

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLabelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: get data from db
        data.addAll(DataHelper.loadLabelEntries())
        binding.labels = data

        // setup recycler view
        binding.labelsRv.adapter = LabelAdapter(data)
        binding.labelsRv.layoutManager = LinearLayoutManager(this)

        // setup popup menu
        popup = PopupMenu(this, binding.labelsMnuBtn).apply {
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete_selected_label_btn -> {
                        data.removeAll(data.filter { it.isChecked.get() })
                        binding.labelsRv.adapter?.notifyDataSetChanged()
                    }
                    R.id.delete_all_label_btn -> {
                        val len = data.size
                        data.clear()
                        binding.labelsRv.adapter?.notifyItemRangeRemoved(0, len)
                    }
                }
                true
            }
        }.also { menuInflater.inflate(R.menu.labels_menu, it.menu) }

        binding.addLabelLl.setOnClickListener {
            data.add(LabelEntry())
            binding.labelsRv.adapter?.notifyItemInserted(data.size)
        }

        binding.labelsMnuBtn.setOnClickListener { popup.show() }
        binding.executePendingBindings()
    }
}