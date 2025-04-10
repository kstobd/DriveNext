package com.example.drivenext.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.drivenext.utils.Result
import com.example.drivenext.domain.model.User
import com.example.drivenext.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Login screen
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : BaseViewModel<LoginViewModel.LoginState, LoginViewModel.LoginEvent, LoginViewModel.LoginEffect>() {

    data class LoginState(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val emailError: String? = null,
        val passwordError: String? = null
    )

    sealed class LoginEvent {
        data class EmailChanged(val email: String) : LoginEvent()
        data class PasswordChanged(val password: String) : LoginEvent()
        object LoginClicked : LoginEvent()
        object RegisterClicked : LoginEvent()
        object RetryConnection : LoginEvent()
    }

    sealed class LoginEffect {
        data class NavigateToHome(val user: User) : LoginEffect()
        object NavigateToRegister : LoginEffect()
        data class ShowError(val message: String) : LoginEffect()
    }

    override fun createInitialState(): LoginState = LoginState()

    override fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                setState { copy(email = event.email, emailError = null) }
            }
            is LoginEvent.PasswordChanged -> {
                setState { copy(password = event.password, passwordError = null) }
            }
            is LoginEvent.LoginClicked -> {
                login()
            }
            is LoginEvent.RegisterClicked -> {
                setEffect(LoginEffect.NavigateToRegister)
            }
            is LoginEvent.RetryConnection -> {
                // Просто обновляем состояние для проверки соединения
            }
        }
    }

    private fun login() {
        val currentState = state.value
        
        // Validate input
        if (!validateInput()) {
            return
        }

        // Set loading state
        setState { copy(isLoading = true) }

        viewModelScope.launch {
            when (val result = loginUseCase(currentState.email, currentState.password)) {
                is Result.Success -> {
                    setState { copy(isLoading = false) }
                    setEffect(LoginEffect.NavigateToHome(result.data))
                }
                is Result.Error -> {
                    val errorMessage = result.exception.message ?: "Неизвестная ошибка"
                    
                    // Обрабатываем разные типы ошибок
                    when {
                        errorMessage.contains("Неверный пароль") -> {
                            setState { 
                                copy(
                                    isLoading = false,
                                    passwordError = "Неверный пароль"
                                )
                            }
                        }
                        errorMessage.contains("Пользователь с таким email не существует") -> {
                            setState { 
                                copy(
                                    isLoading = false,
                                    emailError = "Пользователь с таким email не существует"
                                )
                            }
                        }
                        else -> {
                            setState { 
                                copy(
                                    isLoading = false,
                                    emailError = null,
                                    passwordError = null
                                )
                            }
                            setEffect(LoginEffect.ShowError(errorMessage))
                        }
                    }
                }
                is Result.Loading -> {
                    // Просто ждем, состояние загрузки уже установлено
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        val currentState = state.value
        var isValid = true

        if (currentState.email.isBlank()) {
            setState { copy(emailError = "Email не может быть пустым") }
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            setState { copy(emailError = "Пожалуйста, введите корректный email") }
            isValid = false
        }

        if (currentState.password.isBlank()) {
            setState { copy(passwordError = "Пароль не может быть пустым") }
            isValid = false
        } else if (currentState.password.length < 6) {
            setState { copy(passwordError = "Пароль должен содержать не менее 6 символов") }
            isValid = false
        }

        return isValid
    }
}