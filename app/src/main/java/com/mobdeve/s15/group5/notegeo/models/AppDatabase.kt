package com.mobdeve.s15.group5.notegeo.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Label::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun labelDao(): LabelDao

    companion object : SingletonHolder<AppDatabase, Context> ({
        Room.databaseBuilder(it.applicationContext, AppDatabase::class.java, "notegeo.db")
            .build()
    })
}
