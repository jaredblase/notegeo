package com.mobdeve.s15.group5.notegeo.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mobdeve.s15.group5.notegeo.GeofenceHelperModel
import com.mobdeve.s15.group5.notegeo.editor.NoteEditorViewModel
import com.mobdeve.s15.group5.notegeo.home.HomeViewModel
import com.mobdeve.s15.group5.notegeo.label.LabelViewModel
import com.mobdeve.s15.group5.notegeo.recyclebin.RecycleBinViewModel
import kotlinx.coroutines.CoroutineDispatcher
import java.lang.IllegalArgumentException

class ViewModelFactory(
    private val repository: NoteGeoRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LabelViewModel::class.java) -> {
                LabelViewModel(repository, dispatcher) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository, dispatcher) as T
            }
            modelClass.isAssignableFrom(RecycleBinViewModel::class.java) -> {
                RecycleBinViewModel(repository, dispatcher) as T
            }
            modelClass.isAssignableFrom(NoteEditorViewModel::class.java) -> {
                NoteEditorViewModel() as T
            }

            modelClass.isAssignableFrom(GeofenceHelperModel::class.java) ->{
                GeofenceHelperModel() as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}