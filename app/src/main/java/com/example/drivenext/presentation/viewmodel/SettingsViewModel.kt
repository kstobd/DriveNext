package com.example.drivenext.presentation.viewmodel

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
    private val userRepository: UserRepository
) : BaseViewModel<SettingsViewModel.SettingsState, SettingsViewModel.SettingsEvent, SettingsViewModel.SettingsEffect>() {

    data class SettingsState(
        val user: User? = null,
        val isLoading: Boolean = false,
        val error: String? = null
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
}