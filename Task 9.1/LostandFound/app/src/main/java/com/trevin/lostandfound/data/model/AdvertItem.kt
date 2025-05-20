package com.trevin.lostandfound.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity
data class AdvertItem(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val postType: String,
    val title: String,
    val phoneNumber: String?,
    val description: String?,
    val date: Date?,
    val location: String?,
    val latitude: Double?,
    val longitude: Double?

)