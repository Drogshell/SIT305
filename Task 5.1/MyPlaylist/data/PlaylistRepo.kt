package com.trevin.myplaylists.data

import com.trevin.myplaylists.data.database.PlaylistDao
import com.trevin.myplaylists.data.database.PlaylistDatabase
import com.trevin.myplaylists.data.model.PlaylistItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PlaylistRepo private constructor(private val playlistDao: PlaylistDao) {

    fun getPlaylistFlow(userId: Long): Flow<List<PlaylistItem>> =
        playlistDao.getPlaylistForUser(userId)

    suspend fun addURL(userId: Long, url: String): Long = withContext(Dispatchers.IO) {
        playlistDao.addItem(
            PlaylistItem(userId = userId, youtubeUrl = url)
        )
    }

    suspend fun remove(item: Long) = withContext(Dispatchers.IO) {
        playlistDao.deleteItem(item)
    }

    suspend fun clear(userId: Long) =
        withContext(Dispatchers.IO) { playlistDao.clearPlaylist(userId) }

    companion object {

        @Volatile
        private var INSTANCE: PlaylistRepo? = null

        fun getInstance(database: PlaylistDatabase): PlaylistRepo = INSTANCE ?: synchronized(this) {
            INSTANCE ?: PlaylistRepo(database.playlistDao()).also { INSTANCE = it }
        }

    }

}