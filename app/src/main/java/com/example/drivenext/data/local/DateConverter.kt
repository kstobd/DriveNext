package com.example.drivenext.data.local

import androidx.room.TypeConverter
import java.util.Date

/**
 * Type converter for converting Date objects to/from Long values for Room database
 */
class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}