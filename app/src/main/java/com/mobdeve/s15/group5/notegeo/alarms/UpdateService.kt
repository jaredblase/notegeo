package com.mobdeve.s15.group5.notegeo.alarms

import android.content.Intent
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.mobdeve.s15.group5.notegeo.NoteGeoApplication
import kotlinx.coroutines.launch
import java.util.Date

/**
 * This service updates the note entry in the db that had the alarm.
 * The small update ensures that a UI update (tag strikethrough) will happen just in case the
 * app is still open when an alarm is triggered.
 */
class UpdateService : LifecycleService() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val context = applicationContext as NoteGeoApplication
        val id = intent?.getLongExtra(AlarmReceiver.ID_KEY, -1)

        if (id != null && id != -1L) {
            context.repo.savedNotes.asLiveData().observe(this) { list ->
                lifecycleScope.launch {
                    list.find { it.note._id == id }?.note?.let { context.repo.upsertNote(it.apply {
                        // a small, unnoticeable change
                        dateAlarm = Date(dateAlarm!!.time + 1)
                    }) }
                    stopSelf()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }
}