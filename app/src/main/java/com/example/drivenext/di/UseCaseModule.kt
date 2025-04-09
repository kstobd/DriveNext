package com.example.drivenext.di

import com.example.drivenext.domain.repository.CarRepository
import com.example.drivenext.domain.usecase.GetAvailableCarsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing use cases
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetAvailableCarsUseCase(carRepository: CarRepository): GetAvailableCarsUseCase {
        return GetAvailableCarsUseCase(carRepository)
    }
    
    // Здесь можно добавить другие UseCase по мере необходимости
}