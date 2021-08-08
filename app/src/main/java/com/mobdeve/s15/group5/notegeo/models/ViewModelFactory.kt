package com.mobdeve.s15.group5.notegeo.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mobdeve.s15.group5.notegeo.home.HomeViewModel
import com.mobdeve.s15.group5.notegeo.label.LabelViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory(private val repository: NoteGeoRepository): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LabelViewModel::class.java)) {
            return LabelViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}