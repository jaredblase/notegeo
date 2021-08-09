package com.mobdeve.s15.group5.notegeo.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mobdeve.s15.group5.notegeo.home.HomeViewModel
import com.mobdeve.s15.group5.notegeo.label.LabelViewModel
import com.mobdeve.s15.group5.notegeo.recyclebin.RecycleBinViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory(private val repository: NoteGeoRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LabelViewModel::class.java) -> {
                LabelViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RecycleBinViewModel::class.java) -> {
                RecycleBinViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}