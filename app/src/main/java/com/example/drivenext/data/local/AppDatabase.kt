package com.example.drivenext.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.drivenext.data.local.dao.BookingDao
import com.example.drivenext.data.local.dao.CarDao
import com.example.drivenext.data.local.dao.UserDao
import com.example.drivenext.data.local.entity.BookingEntity
import com.example.drivenext.data.local.entity.CarEntity
import com.example.drivenext.data.local.entity.UserEntity
import com.example.drivenext.utils.DateConverter

/**
 * Основной класс базы данных приложения
 * @property userDao DAO для работы с пользователями
 * @property carDao DAO для работы с автомобилями
 * @property bookingDao DAO для работы с бронированиями
 */
@Database(
    entities = [UserEntity::class, CarEntity::class, BookingEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    // Абстрактные функции для доступа к DAO объектам
    abstract fun userDao(): UserDao
    abstract fun carDao(): CarDao
    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Создает или возвращает существующий экземпляр базы данных
         * @param context Контекст приложения
         * @return Экземпляр базы данных
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "drivenext_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                
                INSTANCE = instance
                
                // Инициализация базы данных тестовыми данными
                DatabaseInitializer.preloadDatabase(instance)
                
                instance
            }
        }
    }
}