package com.mobdeve.s15.group5.notegeo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.mobdeve.s15.group5.notegeo.databinding.ActivityLabelBinding

class LabelActivity : AppCompatActivity() {
    private val data = intArrayOf()
    private lateinit var binding: ActivityLabelBinding
    private lateinit var popup: PopupMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLabelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addLabelLl.setOnClickListener {
            // TODO: Add a component to recycler view
            binding.labelsMnuBtn.visibility = View.VISIBLE
        }

        if (data.isEmpty()) {
            binding.labelsEmptyLl.visibility = View.VISIBLE
//            binding.labelsMnuBtn.visibility = View.GONE
        }

        popup = PopupMenu(this, binding.labelsMnuBtn)
        menuInflater.inflate(R.menu.labels_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            // TODO: Implement
            when (it.itemId) {
                R.id.delete_selected_label_btn-> {
                }
                R.id.delete_all_label_btn-> {
                }
            }

            true
        }

        binding.labelsMnuBtn.setOnClickListener { popup.show() }
    }

}