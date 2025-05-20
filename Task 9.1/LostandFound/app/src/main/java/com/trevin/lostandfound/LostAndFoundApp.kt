package com.trevin.lostandfound

import android.app.Application
import com.trevin.lostandfound.data.AdvertItemsRepository
import com.trevin.lostandfound.data.database.LostAndFoundDatabase


class LostAndFoundApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val database = LostAndFoundDatabase.getDatabase(this)
        val lostAndFoundDao = database.getLostAndFoundDao()
        lostAndFoundRepo = AdvertItemsRepository(lostAndFoundDao)
    }

    companion object {
        lateinit var lostAndFoundRepo: AdvertItemsRepository
    }

}