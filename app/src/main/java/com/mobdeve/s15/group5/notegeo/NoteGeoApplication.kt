package com.mobdeve.s15.group5.notegeo

import android.app.Application
import com.mobdeve.s15.group5.notegeo.models.AppDatabase
import com.mobdeve.s15.group5.notegeo.models.NoteGeoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class NoteGeoApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val db by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repo by lazy { NoteGeoRepository(db.labelDao(), db.noteDao()) }
}