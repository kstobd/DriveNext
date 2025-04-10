package com.example.drivenext.data.local

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Менеджер пользовательских настроек для хранения состояний приложения
 */
@Singleton
class UserPreferencesManager @Inject constructor(context: Context) {
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )
    
    /**
     * Проверяет, был ли пройден онбординг
     * @return true если онбординг был пройден, иначе false
     */
    fun isOnboardingCompleted(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }
    
    /**
     * Устанавливает флаг о прохождении онбординга
     * @param completed статус прохождения онбординга
     */
    fun setOnboardingCompleted(completed: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }
    
    /**
     * Получает сохраненный токен доступа
     * @return токен доступа или null, если токен не найден
     */
    fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }
    
    /**
     * Сохраняет токен доступа
     * @param token токен для сохранения
     */
    fun saveAccessToken(token: String) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }
    
    /**
     * Удаляет токен доступа
     */
    fun clearAccessToken() {
        sharedPreferences.edit().remove(KEY_ACCESS_TOKEN).apply()
    }
    
    companion object {
        private const val PREFS_NAME = "drive_next_preferences"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_ACCESS_TOKEN = "access_token"
    }
}