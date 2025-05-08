package com.trevin.lostandfound.util

import androidx.room.TypeConverter
import java.util.Date

class Converters {

    @TypeConverter
    fun fromDate(date: Date?) : Long? = date?.time

    @TypeConverter
    fun toDate(value: Long?) : Date? {
        return value?.let { Date(it) }
    }

}