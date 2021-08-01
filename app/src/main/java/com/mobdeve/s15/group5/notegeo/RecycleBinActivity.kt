package com.mobdeve.s15.group5.notegeo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mobdeve.s15.group5.notegeo.databinding.ActivityRecycleBinBinding

class RecycleBinActivity : AppCompatActivity() {
    private val data = intArrayOf()
    private lateinit var binding: ActivityRecycleBinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecycleBinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (data.isEmpty()) {
            binding.reycleBinEmptyLl.visibility = View.VISIBLE
        }
    }
}