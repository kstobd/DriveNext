package com.example.drivenext.di

import com.example.drivenext.data.local.dao.BookingDao
import com.example.drivenext.data.local.dao.CarDao
import com.example.drivenext.data.local.dao.UserDao
import com.example.drivenext.data.repository.BookingRepositoryImpl
import com.example.drivenext.data.repository.CarRepositoryImpl
import com.example.drivenext.data.repository.UserRepositoryImpl
import com.example.drivenext.domain.repository.BookingRepository
import com.example.drivenext.domain.repository.CarRepository
import com.example.drivenext.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing repository implementations
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepositoryImpl(userDao)
    }

    @Provides
    @Singleton
    fun provideCarRepository(carDao: CarDao): CarRepository {
        return CarRepositoryImpl(carDao)
    }

    @Provides
    @Singleton
    fun provideBookingRepository(bookingDao: BookingDao): BookingRepository {
        return BookingRepositoryImpl(bookingDao)
    }
}