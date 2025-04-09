package com.example.drivenext.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.drivenext.domain.model.User
import com.example.drivenext.domain.repository.UserRepository
import com.example.drivenext.utils.Result
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
    }

    sealed class RegisterEffect {
        object NavigateToLogin : RegisterEffect()
        data class ShowError(val message: String) : RegisterEffect()
        data class ShowSuccess(val message: String) : RegisterEffect()
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
        }
    }

    private fun register() {
        val state = state.value
        
        // Validate inputs
        if (!validateInputs(
                state.name,
                state.email,
                state.phoneNumber,
                state.password,
                state.confirmPassword
            )
        ) {
            return
        }

        // Create user and register
        setState { copy(isLoading = true) }
        
        val user = User(
            name = state.name,
            email = state.email,
            phoneNumber = state.phoneNumber,
            password = state.password
        )

        viewModelScope.launch {
            // Check if email already exists
            when (val emailCheck = userRepository.getUserByEmail(state.email)) {
                is Result.Success -> {
                    setState { copy(isLoading = false) }
                    setEffect(RegisterEffect.ShowError("Email is already registered"))
                }
                is Result.Error -> {
                    // Email doesn't exist, continue with registration
                    when (val result = userRepository.createUser(user)) {
                        is Result.Success -> {
                            setState { copy(isLoading = false) }
                            setEffect(RegisterEffect.ShowSuccess("Registration successful! Please login."))
                            setEffect(RegisterEffect.NavigateToLogin)
                        }
                        is Result.Error -> {
                            setState { copy(isLoading = false) }
                            setEffect(RegisterEffect.ShowError(result.exception.message ?: "Registration failed"))
                        }
                        is Result.Loading -> {
                            setState { copy(isLoading = true) }
                        }
                    }
                }
                is Result.Loading -> {
                    setState { copy(isLoading = true) }
                }
            }
        }
    }

    private fun validateInputs(
        name: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true

        // Validate name
        if (name.isBlank()) {
            setState { copy(nameError = "Name cannot be empty") }
            isValid = false
        }

        // Validate email
        if (email.isBlank()) {
            setState { copy(emailError = "Email cannot be empty") }
            isValid = false
        } else if (!isValidEmail(email)) {
            setState { copy(emailError = "Please enter a valid email address") }
            isValid = false
        }

        // Validate phone
        if (phone.isBlank()) {
            setState { copy(phoneError = "Phone number cannot be empty") }
            isValid = false
        } else if (!isValidPhone(phone)) {
            setState { copy(phoneError = "Please enter a valid phone number") }
            isValid = false
        }

        // Validate password
        if (password.isBlank()) {
            setState { copy(passwordError = "Password cannot be empty") }
            isValid = false
        } else if (password.length < 6) {
            setState { copy(passwordError = "Password must be at least 6 characters") }
            isValid = false
        }

        // Validate confirm password
        if (confirmPassword.isBlank()) {
            setState { copy(confirmPasswordError = "Confirm password cannot be empty") }
            isValid = false
        } else if (password != confirmPassword) {
            setState { copy(confirmPasswordError = "Passwords do not match") }
            isValid = false
        }

        return isValid
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPhone(phone: String): Boolean {
        return android.util.Patterns.PHONE.matcher(phone).matches()
    }
}