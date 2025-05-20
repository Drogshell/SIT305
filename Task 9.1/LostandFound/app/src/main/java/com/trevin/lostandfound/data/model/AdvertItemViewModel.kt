package com.trevin.lostandfound.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trevin.lostandfound.LostAndFoundApp
import com.trevin.lostandfound.data.AdvertItemsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AdvertItemViewModel : ViewModel() {

    private val repository: AdvertItemsRepository = LostAndFoundApp.lostAndFoundRepo

    fun createAdvert(advert: AdvertItem) {
        viewModelScope.launch {
            repository.createAdvert(advert)
        }
    }

    fun getAdverts(): Flow<List<AdvertItem>> {
        return repository.getAllAdverts()
    }

    // Not sure if I should allow people to update adverts
    fun updateAdvert(advert: AdvertItem) {
        viewModelScope.launch {
            repository.updateAdvert(advert)
        }
    }

    fun deleteAdvert(advert: AdvertItem) {
        viewModelScope.launch {
            repository.deleteAdvert(advert)
        }
    }

}