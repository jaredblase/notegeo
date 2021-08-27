package com.mobdeve.s15.group5.notegeo.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mobdeve.s15.group5.notegeo.models.Note

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra(ID_KEY, 0)
        val title = intent.getStringExtra(TITLE_KEY)!!

        println(id)
        println(title)

        val notificationUtils = NotificationUtils(context)
        val manager = notificationUtils.getManager()
        val notification = notificationUtils.getNotificationBuilder(title).build()
        manager.notify(id.hashCode(), notification)

        val count = manager.activeNotifications.size
        val summary = notificationUtils.getNotificationBuilder()
            .setGroupSummary(true)
            .setNumber(count)
            .build()
        manager.notify(0, summary)
    }

    fun setAlarm(context: Context, note: Note) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val id = note._id
        val date = note.dateAlarm!!

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(ID_KEY, note._id)
            putExtra(TITLE_KEY, note.title)
        }

        PendingIntent.getBroadcast(context, id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT).let {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.time, it)
        }
    }

    fun cancelAlarm(context: Context, note: Note) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val id = note._id

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(ID_KEY, note._id)
            putExtra(TITLE_KEY, note.title)
        }

        PendingIntent.getBroadcast(context, id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT)?.let {
            alarmManager.cancel(it)
        }
    }

    companion object {
        const val ID_KEY = "ID_KEY"
        const val TITLE_KEY = "TITLE_KEY"
    }
}