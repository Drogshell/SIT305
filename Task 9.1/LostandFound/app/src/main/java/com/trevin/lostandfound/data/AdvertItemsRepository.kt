package com.trevin.lostandfound.data

import com.trevin.lostandfound.data.database.LostAndFoundDao
import com.trevin.lostandfound.data.model.AdvertItem
import kotlinx.coroutines.flow.Flow

class AdvertItemsRepository(private val lostAndFoundDao: LostAndFoundDao) {

    suspend fun createAdvert(advertItem: AdvertItem) {
        lostAndFoundDao.createAdvert(advertItem)
    }

    fun getAllAdverts(): Flow<List<AdvertItem>> {
        return lostAndFoundDao.getAllAdverts()
    }

    suspend fun updateAdvert(advertItem: AdvertItem) {
        lostAndFoundDao.updateAdvert(advertItem)
    }

    suspend fun deleteAdvert(advertItem: AdvertItem) {
        lostAndFoundDao.deleteAdvert(advertItem)
    }

}