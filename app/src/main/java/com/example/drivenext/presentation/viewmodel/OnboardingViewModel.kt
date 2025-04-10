package com.example.drivenext.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.drivenext.R
import com.example.drivenext.data.local.UserPreferencesManager
import com.example.drivenext.domain.model.OnboardingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel для экрана онбординга
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {
    
    // Список элементов для отображения на экране онбординга
    val onboardingItems = listOf(
        OnboardingItem(
            imageRes = R.drawable.onboarding_image_1,
            titleRes = R.string.onboarding_title_1,
            descriptionRes = R.string.onboarding_description_1
        ),
        OnboardingItem(
            imageRes = R.drawable.onboarding_image_2,
            titleRes = R.string.onboarding_title_2,
            descriptionRes = R.string.onboarding_description_2
        ),
        OnboardingItem(
            imageRes = R.drawable.onboarding_image_3,
            titleRes = R.string.onboarding_title_3,
            descriptionRes = R.string.onboarding_description_3
        )
    )
    
    /**
     * Завершает процесс онбординга
     */
    fun completeOnboarding() {
        userPreferencesManager.setOnboardingCompleted(true)
    }
    
    /**
     * Проверяет, был ли пройден онбординг
     */
    fun isOnboardingCompleted(): Boolean {
        return userPreferencesManager.isOnboardingCompleted()
    }
    
    /**
     * Проверяет наличие действительного токена доступа
     */
    fun hasValidToken(): Boolean {
        return !userPreferencesManager.getAccessToken().isNullOrEmpty()
    }
}