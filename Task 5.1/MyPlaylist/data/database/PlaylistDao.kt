package com.trevin.myplaylists.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.trevin.myplaylists.data.model.PlaylistItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addItem(item: PlaylistItem): Long

    @Query("SELECT * FROM playlist_items WHERE user_id = :userId")
    fun getPlaylistForUser(userId: Long): Flow<List<PlaylistItem>>

    @Query("DELETE FROM playlist_items WHERE id = :itemId")
    suspend fun deleteItem(itemId: Long)

    @Query("DELETE FROM playlist_items WHERE user_id = :userId")
    suspend fun clearPlaylist(userId: Long)

}