package com.trevin.myplaylists.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.trevin.myplaylists.data.model.PlaylistItem
import com.trevin.myplaylists.data.model.User

@Database(entities = [User::class, PlaylistItem::class], version = 1)
abstract class PlaylistDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun playlistDao(): PlaylistDao

    companion object {

        @Volatile
        private var INSTANCE: PlaylistDatabase? = null

        fun getInstance(context: Context): PlaylistDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                PlaylistDatabase::class.java,
                "playlist_database"
            ).fallbackToDestructiveMigration(true).build().also { INSTANCE = it }
        }

    }

}