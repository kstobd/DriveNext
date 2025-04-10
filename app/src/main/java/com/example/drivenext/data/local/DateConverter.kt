package com.example.drivenext.data.local

import androidx.room.TypeConverter
import java.util.Date

/**
 * Конвертер для преобразования объектов Date в значения Long и обратно для базы данных Room
 */
class DateConverter {
    /**
     * Преобразует временную метку (timestamp) в объект Date
     * @param value Временная метка в миллисекундах
     * @return Объект Date или null
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Преобразует объект Date в временную метку (timestamp)
     * @param date Объект Date для конвертации
     * @return Временная метка в миллисекундах или null
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}