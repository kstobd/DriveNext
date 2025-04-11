package com.example.drivenext.di

import android.content.Context
import android.content.SharedPreferences
import com.example.drivenext.data.local.UserPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Модуль Dagger Hilt для предоставления зависимостей на уровне приложения
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * Предоставляет экземпляр UserPreferencesManager для управления пользовательскими настройками
     * @param context Контекст приложения
     * @return Экземпляр UserPreferencesManager
     */
    @Provides
    @Singleton
    fun provideUserPreferencesManager(@ApplicationContext context: Context): UserPreferencesManager {
        return UserPreferencesManager(context)
    }
    
    /**
     * Предоставляет экземпляр SharedPreferences для хранения данных приложения
     * @param context Контекст приложения
     * @return Экземпляр SharedPreferences
     */
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("drivenext_preferences", Context.MODE_PRIVATE)
    }
}