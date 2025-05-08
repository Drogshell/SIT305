package com.trevin.lostandfound.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.trevin.lostandfound.data.model.AdvertItem
import kotlinx.coroutines.flow.Flow

@Dao
interface LostAndFoundDao {

    @Insert
    suspend fun createAdvert(advert: AdvertItem)

    @Update
    suspend fun updateAdvert(advert: AdvertItem)

    @Query("SELECT * FROM AdvertItem")
    fun getAllAdverts(): Flow<List<AdvertItem>>

    @Delete
    suspend fun deleteAdvert(advert: AdvertItem)

}