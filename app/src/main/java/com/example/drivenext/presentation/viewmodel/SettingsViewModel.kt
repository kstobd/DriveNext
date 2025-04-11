package com.example.drivenext.presentation.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.example.drivenext.domain.model.User
import com.example.drivenext.domain.repository.UserRepository
import com.example.drivenext.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для экрана настроек
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sharedPreferences: SharedPreferences
) : BaseViewModel<SettingsViewModel.SettingsState, SettingsViewModel.SettingsEvent, SettingsViewModel.SettingsEffect>() {

    data class SettingsState(
        val user: User? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val userPhotoUri: String? = null
    )

    sealed class SettingsEvent {
        data class LoadUserData(val userId: Long) : SettingsEvent()
        object ProfileClicked : SettingsEvent()
        // Другие события для обработки нажатий на настройки
    }

    sealed class SettingsEffect {
        data class NavigateToProfile(val userId: Long) : SettingsEffect()
        data class ShowError(val message: String) : SettingsEffect()
    }

    override fun createInitialState(): SettingsState = SettingsState()

    override fun handleEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.LoadUserData -> {
                loadUserData(event.userId)
                loadUserPhoto(event.userId)
            }
            is SettingsEvent.ProfileClicked -> {
                state.value.user?.let { user ->
                    setEffect(SettingsEffect.NavigateToProfile(user.id))
                }
            }
        }
    }

    /**
     * Загружает данные пользователя из репозитория
     */
    private fun loadUserData(userId: Long) {
        setState { copy(isLoading = true, error = null) }
        
        // Для тестирования - сохраняем тестовую фотографию
        testSavePhoto(userId)
        
        viewModelScope.launch {
            when (val result = userRepository.getUserById(userId)) {
                is Result.Success -> {
                    setState { copy(user = result.data, isLoading = false) }
                }
                is Result.Error -> {
                    setState { 
                        copy(
                            isLoading = false, 
                            error = result.exception.message ?: "Ошибка при загрузке данных пользователя"
                        ) 
                    }
                    setEffect(SettingsEffect.ShowError(result.exception.message ?: "Ошибка при загрузке данных пользователя"))
                }
                is Result.Loading -> {
                    setState { copy(isLoading = true) }
                }
            }
        }
    }
    
    /**
     * Тестовая функция для сохранения изображения
     * Только для отладки проблемы с фотографией
     */
    private fun testSavePhoto(userId: Long) {
        // Сохраняем тестовый URI
        val testUri = "content://com.android.providers.media.documents/document/image%3A12345"
        val photoKey = "user_photo_$userId"
        
        android.util.Log.d("SettingsViewModel", "Saving test photo URI: $testUri for key: $photoKey")
        
        sharedPreferences.edit()
            .putString(photoKey, testUri)
            .apply()
    }
    
    /**
     * Загружает фотографию пользователя из SharedPreferences
     */
    private fun loadUserPhoto(userId: Long) {
        val photoKey = "user_photo_$userId"
        val photoUri = sharedPreferences.getString(photoKey, null)
        
        // Добавляем отладочный вывод
        android.util.Log.d("SettingsViewModel", "Loading user photo for userId=$userId, key=$photoKey, photoUri=$photoUri")
        
        setState { copy(userPhotoUri = photoUri) }
    }
}