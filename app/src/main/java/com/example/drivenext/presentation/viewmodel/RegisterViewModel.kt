package com.example.drivenext.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.drivenext.domain.model.User
import com.example.drivenext.domain.repository.UserRepository
import com.example.drivenext.utils.Result
import com.example.drivenext.util.PasswordUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for handling user registration
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel<RegisterViewModel.RegisterState, RegisterViewModel.RegisterEvent, RegisterViewModel.RegisterEffect>() {

    data class RegisterState(
        val name: String = "",
        val email: String = "",
        val phoneNumber: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val isLoading: Boolean = false,
        val nameError: String? = null,
        val emailError: String? = null,
        val phoneError: String? = null,
        val passwordError: String? = null,
        val confirmPasswordError: String? = null
    )

    sealed class RegisterEvent {
        data class NameChanged(val name: String) : RegisterEvent()
        data class EmailChanged(val email: String) : RegisterEvent()
        data class PhoneChanged(val phone: String) : RegisterEvent()
        data class PasswordChanged(val password: String) : RegisterEvent()
        data class ConfirmPasswordChanged(val confirmPassword: String) : RegisterEvent()
        object RegisterClicked : RegisterEvent()
        object LoginClicked : RegisterEvent()
        object RetryConnection : RegisterEvent()
    }

    sealed class RegisterEffect {
        object NavigateToLogin : RegisterEffect()
        object NavigateToRegisterStep2 : RegisterEffect()
        data class ShowError(val message: String) : RegisterEffect()
        data class ShowSuccess(val message: String) : RegisterEffect()
        data class NavigateToRegisterStep2WithId(val userId: Long) : RegisterEffect()
    }

    override fun createInitialState(): RegisterState = RegisterState()

    override fun handleEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.NameChanged -> {
                setState { copy(name = event.name, nameError = null) }
            }
            is RegisterEvent.EmailChanged -> {
                setState { copy(email = event.email, emailError = null) }
            }
            is RegisterEvent.PhoneChanged -> {
                setState { copy(phoneNumber = event.phone, phoneError = null) }
            }
            is RegisterEvent.PasswordChanged -> {
                setState { copy(password = event.password, passwordError = null) }
            }
            is RegisterEvent.ConfirmPasswordChanged -> {
                setState { copy(confirmPassword = event.confirmPassword, confirmPasswordError = null) }
            }
            is RegisterEvent.RegisterClicked -> {
                register()
            }
            is RegisterEvent.LoginClicked -> {
                setEffect(RegisterEffect.NavigateToLogin)
            }
            is RegisterEvent.RetryConnection -> {
                // Просто обновляем состояние для проверки соединения
            }
        }
    }

    /**
     * Метод для регистрации пользователя
     * Вызывается при нажатии на кнопку "Далее"
     */
    fun register() {
        val state = state.value
        
        // Validate inputs
        if (!validateInputs(
                state.email,
                state.password,
                state.confirmPassword
            )
        ) {
            return
        }

        // Create user and register
        setState { copy(isLoading = true) }
        
        // Хешируем пароль перед созданием пользователя
        val hashedPassword = PasswordUtils.hashPassword(state.password)
        
        // Создаем пользователя с обязательными полями, остальные поля будут заполнены на втором шаге
        val user = User(
            name = state.name,
            email = state.email,
            phoneNumber = state.phoneNumber,
            password = hashedPassword,
            firstName = "",
            lastName = "",
            middleName = "",
            birthDate = null,
            gender = ""
        )

        viewModelScope.launch {
            try {
                // Check if email already exists
                when (val emailCheck = userRepository.getUserByEmail(state.email)) {
                    is Result.Success -> {
                        setState { copy(isLoading = false) }
                        setEffect(RegisterEffect.ShowError("Email уже зарегистрирован"))
                    }
                    is Result.Error -> {
                        // Email doesn't exist, continue with registration
                        try {
                            when (val result = userRepository.createUser(user)) {
                                is Result.Success -> {
                                    setState { copy(isLoading = false) }
                                    // Передаем ID пользователя в следующий экран
                                    val userId = result.data
                                    if (userId > 0) {
                                        setEffect(RegisterEffect.NavigateToRegisterStep2WithId(userId))
                                    } else {
                                        setEffect(RegisterEffect.ShowError("Не удалось получить ID пользователя"))
                                    }
                                }
                                is Result.Error -> {
                                    setState { copy(isLoading = false) }
                                    setEffect(RegisterEffect.ShowError(result.exception.message ?: "Регистрация не удалась"))
                                }
                                is Result.Loading -> {
                                    setState { copy(isLoading = true) }
                                }
                            }
                        } catch (e: Exception) {
                            setState { copy(isLoading = false) }
                            setEffect(RegisterEffect.ShowError("Ошибка при создании пользователя: ${e.message ?: "Неизвестная ошибка"}"))
                        }
                    }
                    is Result.Loading -> {
                        setState { copy(isLoading = true) }
                    }
                }
            } catch (e: Exception) {
                setState { copy(isLoading = false) }
                setEffect(RegisterEffect.ShowError("Произошла ошибка: ${e.message ?: "Неизвестная ошибка"}"))
            }
        }
    }

    /**
     * Проверяет валидность введённых данных при регистрации
     */
    private fun validateInputs(
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true

        // Validate name
        if (state.value.name.isBlank()) {
            setState { copy(nameError = "Имя не может быть пустым") }
            isValid = false
        }

        // Validate email
        if (email.isBlank()) {
            setState { copy(emailError = "Email не может быть пустым") }
            isValid = false
        } else if (!isValidEmail(email)) {
            setState { copy(emailError = "Введите корректный адрес электронной почты.") }
            isValid = false
        }

        // Validate password
        if (password.isBlank()) {
            setState { copy(passwordError = "Пароль не может быть пустым") }
            isValid = false
        } else if (password.length < 6) {
            setState { copy(passwordError = "Пароль должен содержать не менее 6 символов") }
            isValid = false
        }

        // Validate confirm password
        if (confirmPassword.isBlank()) {
            setState { copy(confirmPasswordError = "Повторите пароль") }
            isValid = false
        } else if (password != confirmPassword) {
            setState { copy(confirmPasswordError = "Пароли не совпадают.") }
            isValid = false
        }

        return isValid
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Метод для повторной проверки подключения к интернету
     * Вызывается при нажатии на кнопку "Повторить попытку" на экране отсутствия подключения
     */
    fun checkConnection() {
        // Здесь можно добавить логику повторной проверки подключения
        // Этот метод будет вызываться при нажатии кнопки "Повторить попытку"
    }
}