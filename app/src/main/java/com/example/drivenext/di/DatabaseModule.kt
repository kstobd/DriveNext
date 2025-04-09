package com.example.drivenext.di

import android.content.Context
import com.example.drivenext.data.local.AppDatabase
import com.example.drivenext.data.local.dao.BookingDao
import com.example.drivenext.data.local.dao.CarDao
import com.example.drivenext.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideCarDao(database: AppDatabase): CarDao {
        return database.carDao()
    }

    @Provides
    @Singleton
    fun provideBookingDao(database: AppDatabase): BookingDao {
        return database.bookingDao()
    }
}