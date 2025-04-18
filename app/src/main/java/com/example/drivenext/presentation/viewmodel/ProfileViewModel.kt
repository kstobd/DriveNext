package com.example.drivenext.presentation.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.viewModelScope
import com.example.drivenext.domain.model.User
import com.example.drivenext.domain.repository.UserRepository
import com.example.drivenext.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для экрана профиля пользователя
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sharedPreferences: SharedPreferences
) : BaseViewModel<ProfileViewModel.ProfileState, ProfileViewModel.ProfileEvent, ProfileViewModel.ProfileEffect>() {

    data class ProfileState(
        val user: User? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val userPhotoUri: String? = null
    )

    sealed class ProfileEvent {
        data class LoadUserData(val userId: Long) : ProfileEvent()
    }

    sealed class ProfileEffect {
        data class ShowError(val message: String) : ProfileEffect()
    }

    override fun createInitialState(): ProfileState = ProfileState()

    override fun handleEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadUserData -> {
                loadUserData(event.userId)
                loadUserPhoto(event.userId)
            }
        }
    }

    /**
     * Загружает данные пользователя из репозитория
     */
    private fun loadUserData(userId: Long) {
        setState { copy(isLoading = true, error = null) }
        
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
                    setEffect(ProfileEffect.ShowError(result.exception.message ?: "Ошибка при загрузке данных пользователя"))
                }
                is Result.Loading -> {
                    setState { copy(isLoading = true) }
                }
            }
        }
    }
    
    /**
     * Загружает фотографию пользователя из SharedPreferences
     */
    private fun loadUserPhoto(userId: Long) {
        val photoKey = "user_photo_$userId"
        val photoUri = sharedPreferences.getString(photoKey, null)
        
        // Добавляем отладочный вывод
        android.util.Log.d("ProfileViewModel", "Loading user photo for userId=$userId, key=$photoKey, photoUri=$photoUri")
        
        setState { copy(userPhotoUri = photoUri) }
    }
}