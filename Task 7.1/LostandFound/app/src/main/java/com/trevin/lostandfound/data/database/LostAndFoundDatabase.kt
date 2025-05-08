package com.trevin.lostandfound.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.trevin.lostandfound.data.model.AdvertItem
import com.trevin.lostandfound.util.Converters

@Database(entities = [AdvertItem::class], version = 1)
@TypeConverters(Converters::class)
abstract class LostAndFoundDatabase : RoomDatabase() {

    abstract fun getLostAndFoundDao() : LostAndFoundDao

    companion object {

        @Volatile
        private var DATABASE_INSTANCE: LostAndFoundDatabase? = null

        fun getDatabase(context: Context) : LostAndFoundDatabase {

            return DATABASE_INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context,
                    LostAndFoundDatabase::class.java,
                    "lost-and-found-database"
                ).build()
                DATABASE_INSTANCE = instance
                instance
            }
        }
    }
}