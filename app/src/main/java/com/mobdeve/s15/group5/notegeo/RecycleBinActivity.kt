package com.mobdeve.s15.group5.notegeo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.mobdeve.s15.group5.notegeo.databinding.ActivityRecycleBinBinding

class RecycleBinActivity : AppCompatActivity() {
    private val data = intArrayOf()
    private lateinit var binding: ActivityRecycleBinBinding
    private lateinit var popup: PopupMenu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecycleBinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (data.isEmpty()) {
            binding.reycleBinEmptyLl.visibility = View.VISIBLE
//            binding.recycleBinMnuBtn.visibility = View.GONE
//            return
        }

        popup = PopupMenu(this, binding.recycleBinMnuBtn)
        menuInflater.inflate(R.menu.recycle_bin_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            // TODO: Implement
            when (it.itemId) {
                R.id.delete_all_btn -> {
                }

                R.id.restore_all_btn -> {
                }
            }

            true
        }

        binding.recycleBinMnuBtn.setOnClickListener { popup.show() }
    }
}