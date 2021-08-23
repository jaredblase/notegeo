package com.mobdeve.s15.group5.notegeo.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@Database(entities = [Note::class, Label::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun labelDao(): LabelDao
    abstract fun noteDao(): NoteDao

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
                scope.launch { populateDatabase(it.labelDao(), it.noteDao()) }
            }
        }

        suspend fun populateDatabase(labelDao: LabelDao, noteDao: NoteDao) {
            // add data
            noteDao.insert(
                Note(0, "Sample", "Lorem Ipsum Brodie", -13070788),
                Note(
                    0,
                    "Hello!",
                    "Just some random text to wee wee. Need to make this note a bit more longer so we can see a difference in the layout",
                    -35002,
                    1
                ),
                Note(
                    0,
                    "Test try...",
                    "Is this cool or what? Kotlin master race OwO. Java sucks",
                    -15262682,
                    2
                ),
                Note(
                    0,
                    "This must be a deleted note?",
                    "It should be found in the recycle bin. If not, well...",
                    -16743537,
                    label = 3,
                    dateDeleted = Date(),
                ),
                Note(
                    0,
                    "Another deleted note",
                    "Just to make sure if it works, know what I'm saying?",
                    dateDeleted = Date()
                )
            )
            labelDao.insert(Label(1, "For home"), Label(2, "Exercise"), Label(3, "Academics"))
        }
    }
}
