package com.mobdeve.s15.group5.notegeo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobdeve.s15.group5.notegeo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.sideMenuBtn.setOnClickListener {
            view.open()
        }

        binding.drawerMenu.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.reminders_menu_btn -> {

                }

                R.id.deleted_menu_btn -> {
                    startActivity(Intent(this, RecycleBinActivity::class.java))
                }

                R.id.labels_menu_btn -> {
                    startActivity(Intent(this, LabelActivity::class.java))
                }

            }

            view.closeDrawers()
            true
        }

        binding.addNoteFab.setOnClickListener { 
            startActivity(Intent(this, EditNoteActivity::class.java))
        }
    }
}