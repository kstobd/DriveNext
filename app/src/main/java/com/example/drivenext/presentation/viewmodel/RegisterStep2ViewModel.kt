package com.example.drivenext.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.drivenext.domain.model.User
import com.example.drivenext.domain.repository.UserRepository
import com.example.drivenext.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel для второго шага регистрации с дополнительными данными пользователя
 */
@HiltViewModel
class RegisterStep2ViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel<RegisterStep2ViewModel.RegisterStep2State, RegisterStep2ViewModel.RegisterStep2Event, RegisterStep2ViewModel.RegisterStep2Effect>() {

    override fun createInitialState(): RegisterStep2State {
        return RegisterStep2State()
    }

    data class RegisterStep2State(
        val lastName: String = "",
        val lastNameError: String? = null,
        val firstName: String = "",
        val firstNameError: String? = null,
        val middleName: String = "", // необязательное поле
        val birthDate: String = "",
        val birthDateError: String? = null,
        val gender: Gender? = null,
        val genderError: String? = null,
        val isLoading: Boolean = false,
        val userId: Long? = null // ID пользователя с первого шага
    )

    sealed class RegisterStep2Effect {
        object NavigateToHome : RegisterStep2Effect()
        object NavigateBack : RegisterStep2Effect()
        data class NavigateToRegisterStep3(val userId: Long) : RegisterStep2Effect()
        data class ShowError(val message: String) : RegisterStep2Effect()
        data class ShowSuccess(val message: String) : RegisterStep2Effect()
    }

    sealed class RegisterStep2Event {
        data class LastNameChanged(val lastName: String) : RegisterStep2Event()
        data class FirstNameChanged(val firstName: String) : RegisterStep2Event()
        data class MiddleNameChanged(val middleName: String) : RegisterStep2Event()
        data class BirthDateChanged(val birthDate: String) : RegisterStep2Event()
        data class GenderChanged(val gender: Gender) : RegisterStep2Event()
        data class SetUserId(val userId: Long) : RegisterStep2Event()
        object NextClicked : RegisterStep2Event()
        object BackClicked : RegisterStep2Event()
    }

    enum class Gender {
        MALE, FEMALE
    }

    override fun handleEvent(event: RegisterStep2Event) {
        when (event) {
            is RegisterStep2Event.LastNameChanged -> {
                setState { copy(lastName = event.lastName, lastNameError = null) }
            }
            is RegisterStep2Event.FirstNameChanged -> {
                setState { copy(firstName = event.firstName, firstNameError = null) }
            }
            is RegisterStep2Event.MiddleNameChanged -> {
                setState { copy(middleName = event.middleName) }
            }
            is RegisterStep2Event.BirthDateChanged -> {
                setState { copy(birthDate = event.birthDate, birthDateError = null) }
            }
            is RegisterStep2Event.GenderChanged -> {
                setState { copy(gender = event.gender, genderError = null) }
            }
            is RegisterStep2Event.SetUserId -> {
                setState { copy(userId = event.userId) }
            }
            is RegisterStep2Event.NextClicked -> {
                if (validateInputs()) {
                    completeRegistration()
                }
            }
            is RegisterStep2Event.BackClicked -> {
                setEffect(RegisterStep2Effect.NavigateBack)
            }
        }
    }

    /**
     * Метод для завершения регистрации и обновления данных пользователя
     */
    private fun completeRegistration() {
        val currentState = state.value
        setState { copy(isLoading = true) }

        viewModelScope.launch {
            val userId = currentState.userId ?: run {
                setState { copy(isLoading = false) }
                setEffect(RegisterStep2Effect.ShowError("Ошибка: ID пользователя отсутствует"))
                return@launch
            }
            
            // Получаем текущие данные пользователя
            when (val userResult = userRepository.getUserById(userId)) {
                is Result.Success -> {
                    val user = userResult.data
                    
                    // Обновляем данные пользователя
                    val updatedUser = user.copy(
                        lastName = currentState.lastName,
                        firstName = currentState.firstName,
                        middleName = currentState.middleName,
                        birthDate = parseDate(currentState.birthDate),
                        gender = when (currentState.gender) {
                            Gender.MALE -> "MALE"
                            Gender.FEMALE -> "FEMALE"
                            null -> ""
                        }
                    )
                    
                    // Сохраняем обновленные данные пользователя
                    when (val updateResult = userRepository.updateUser(updatedUser)) {
                        is Result.Success -> {
                            setState { copy(isLoading = false) }
                            setEffect(RegisterStep2Effect.ShowSuccess("Личные данные сохранены"))
                            setEffect(RegisterStep2Effect.NavigateToRegisterStep3(userId))
                        }
                        is Result.Error -> {
                            setState { copy(isLoading = false) }
                            setEffect(RegisterStep2Effect.ShowError(updateResult.exception.message ?: "Ошибка обновления данных"))
                        }
                        is Result.Loading -> {
                            // Ожидаем
                        }
                    }
                }
                is Result.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect(RegisterStep2Effect.ShowError(userResult.exception.message ?: "Пользователь не найден"))
                }
                is Result.Loading -> {
                    // Ожидаем
                }
            }
        }
    }

    /**
     * Проверяет корректность введенных данных
     */
    private fun validateInputs(): Boolean {
        val currentState = state.value
        var isValid = true

        // Проверка фамилии
        if (currentState.lastName.isBlank()) {
            setState { copy(lastNameError = "Фамилия обязательна для заполнения") }
            isValid = false
        }

        // Проверка имени
        if (currentState.firstName.isBlank()) {
            setState { copy(firstNameError = "Имя обязательно для заполнения") }
            isValid = false
        }

        // Проверка даты рождения
        if (currentState.birthDate.isBlank()) {
            setState { copy(birthDateError = "Дата рождения обязательна для заполнения") }
            isValid = false
        } else if (!isValidDate(currentState.birthDate)) {
            setState { copy(birthDateError = "Введите корректную дату рождения (MM.DD.YYYY)") }
            isValid = false
        }

        // Проверка пола
        if (currentState.gender == null) {
            setState { copy(genderError = "Пожалуйста, выберите пол") }
            isValid = false
        }

        if (!isValid) {
            setEffect(RegisterStep2Effect.ShowError("Пожалуйста, заполните все обязательные поля."))
        }

        return isValid
    }

    /**
     * Проверяет корректность формата даты
     */
    private fun isValidDate(dateString: String): Boolean {
        return try {
            val format = SimpleDateFormat("MM.dd.yyyy", Locale.getDefault())
            format.isLenient = false
            format.parse(dateString)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Преобразует строку даты в объект Date
     */
    private fun parseDate(dateString: String): Date? {
        return try {
            val format = SimpleDateFormat("MM.dd.yyyy", Locale.getDefault())
            format.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
}