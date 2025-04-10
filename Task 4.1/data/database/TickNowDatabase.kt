package com.trevin.ticknow.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.trevin.ticknow.data.model.Task
import com.trevin.ticknow.data.model.TaskList

@Database(entities = [Task::class, TaskList::class], version = 5)
abstract class TickNowDatabase : RoomDatabase() {

    abstract fun getTaskDao(): TaskDao
    abstract fun getTaskListDao(): TaskListDao

    companion object {

        @Volatile
        private var DATABASE_INSTANCE: TickNowDatabase? = null

        // Keeps old records of tasks without being destructive
        private val MIGRATION_2_TO_3 = object : Migration(2,3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(""""
                    CREATE TABLE IF NOT EXISTS 'task_list'
                    (
                        'task_list_id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        'name' TEXT NOT NULL
                    )
                    
                    """.trimMargin())
            }
        }

        fun getDatabase(context: Context): TickNowDatabase {

            // This code follows the singleton pattern and ensures that only once instance of the
            // database is ever created and used. It's also thread safe.
            return DATABASE_INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    TickNowDatabase::class.java,
                    "tick-now-database"
                )
                    .addMigrations(MIGRATION_2_TO_3)
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback(){
                        // This is how to seed data
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL("INSERT INTO TaskList (name) VALUES ('Tasks')")
                        }
                    })
                    .build()
                DATABASE_INSTANCE = instance
                instance // return implicit
            }
        }
    }
}