package com.mobdeve.s15.group5.notegeo.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Label::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun labelDao(): LabelDao

    // singleton implementation for the database
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notegeo_db"
                )
                    .fallbackToDestructiveMigration()   // for development purposes
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

    // initialize data in db
    private class AppDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let {
                scope.launch { populateDatabase(it.labelDao()) }
            }
        }

        suspend fun populateDatabase(labelDao: LabelDao) {
            labelDao.clearTable()
            // add data
            labelDao.insert(Label(0, "For home"), Label(0, "Exercise"), Label(0, "Academics"))
        }
    }
}
