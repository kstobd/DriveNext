package com.example.drivenext.presentation.viewmodel

import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.drivenext.domain.repository.UserRepository
import com.example.drivenext.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel для третьего шага регистрации - загрузки документов пользователя
 */
@HiltViewModel
class RegisterStep3ViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sharedPreferences: SharedPreferences
) : BaseViewModel<RegisterStep3ViewModel.RegisterStep3State, RegisterStep3ViewModel.RegisterStep3Event, RegisterStep3ViewModel.RegisterStep3Effect>() {

    data class RegisterStep3State(
        val userId: Long = 0L,
        val driverLicenseNumber: String = "",
        val driverLicenseIssueDate: String = "",
        val profileImageUri: Uri? = null,
        val driverLicenseImageUri: Uri? = null,
        val passportImageUri: Uri? = null,
        val isLoading: Boolean = false,
        val licenseNumberError: String? = null,
        val licenseDateError: String? = null,
        val documentsError: String? = null
    )

    sealed class RegisterStep3Event {
        data class SetUserId(val userId: Long) : RegisterStep3Event()
        data class LicenseNumberChanged(val number: String) : RegisterStep3Event()
        data class LicenseDateChanged(val date: String) : RegisterStep3Event()
        data class ProfileImageSelected(val uri: Uri?) : RegisterStep3Event()
        data class DriverLicenseImageSelected(val uri: Uri?) : RegisterStep3Event()
        data class PassportImageSelected(val uri: Uri?) : RegisterStep3Event()
        object SubmitClicked : RegisterStep3Event()
        object BackClicked : RegisterStep3Event()
    }

    sealed class RegisterStep3Effect {
        object NavigateToHome : RegisterStep3Effect()
        object NavigateBack : RegisterStep3Effect()
        data class ShowError(val message: String) : RegisterStep3Effect()
        data class ShowSuccess(val message: String) : RegisterStep3Effect()
    }

    override fun createInitialState(): RegisterStep3State = RegisterStep3State()

    override fun handleEvent(event: RegisterStep3Event) {
        when (event) {
            is RegisterStep3Event.SetUserId -> {
                setState { copy(userId = event.userId) }
            }
            is RegisterStep3Event.LicenseNumberChanged -> {
                setState { copy(driverLicenseNumber = event.number, licenseNumberError = null) }
            }
            is RegisterStep3Event.LicenseDateChanged -> {
                setState { copy(driverLicenseIssueDate = event.date, licenseDateError = null) }
            }
            is RegisterStep3Event.ProfileImageSelected -> {
                val uri = event.uri
                setState { copy(profileImageUri = uri) }
                
                // Сохраняем URI фотографии профиля в SharedPreferences
                if (uri != null && state.value.userId > 0) {
                    saveProfileImageUri(state.value.userId, uri.toString())
                }
            }
            is RegisterStep3Event.DriverLicenseImageSelected -> {
                setState { copy(driverLicenseImageUri = event.uri, documentsError = null) }
            }
            is RegisterStep3Event.PassportImageSelected -> {
                setState { copy(passportImageUri = event.uri, documentsError = null) }
            }
            is RegisterStep3Event.SubmitClicked -> {
                completeRegistration()
            }
            is RegisterStep3Event.BackClicked -> {
                setEffect(RegisterStep3Effect.NavigateBack)
            }
        }
    }

    /**
     * Сохраняет URI фотографии профиля в SharedPreferences
     */
    private fun saveProfileImageUri(userId: Long, uriString: String) {
        sharedPreferences.edit()
            .putString("user_photo_$userId", uriString)
            .apply()
    }

    /**
     * Завершает регистрацию пользователя, сохраняя данные о документах
     */
    private fun completeRegistration() {
        val currentState = state.value
        
        // Проверка валидности введенных данных
        if (!validateInputs()) {
            return
        }

        // Устанавливаем состояние загрузки
        setState { copy(isLoading = true) }
        
        viewModelScope.launch {
            try {
                // Получаем пользователя по ID
                when (val result = userRepository.getUserById(currentState.userId)) {
                    is Result.Success -> {
                        val user = result.data
                        
                        // Сохраняем фотографию профиля в SharedPreferences, если она была выбрана
                        currentState.profileImageUri?.let { 
                            saveProfileImageUri(currentState.userId, it.toString())
                        }
                        
                        // Обновляем данные пользователя с информацией о документах
                        // В реальном приложении здесь должна быть загрузка файлов на сервер
                        
                        // Имитируем успешное завершение регистрации
                        setState { copy(isLoading = false) }
                        setEffect(RegisterStep3Effect.ShowSuccess("Регистрация успешно завершена!"))
                        setEffect(RegisterStep3Effect.NavigateToHome)
                    }
                    is Result.Error -> {
                        setState { copy(isLoading = false) }
                        setEffect(RegisterStep3Effect.ShowError("Ошибка при обновлении данных: ${result.exception.message ?: "Неизвестная ошибка"}"))
                    }
                    is Result.Loading -> {
                        // Состояние загрузки уже установлено выше
                    }
                }
            } catch (e: Exception) {
                setState { copy(isLoading = false) }
                setEffect(RegisterStep3Effect.ShowError("Произошла ошибка: ${e.message ?: "Неизвестная ошибка"}"))
            }
        }
    }

    /**
     * Проверяет корректность введенных данных
     * @return true если все данные валидны, false в противном случае
     */
    private fun validateInputs(): Boolean {
        val currentState = state.value
        var isValid = true

        // Проверка номера водительского удостоверения
        if (currentState.driverLicenseNumber.isBlank()) {
            setState { copy(licenseNumberError = "Пожалуйста, заполните все обязательные поля.") }
            isValid = false
        }

        // Проверка даты выдачи
        if (currentState.driverLicenseIssueDate.isBlank()) {
            setState { copy(licenseDateError = "Пожалуйста, заполните все обязательные поля.") }
            isValid = false
        } else if (!isValidDate(currentState.driverLicenseIssueDate)) {
            setState { copy(licenseDateError = "Введите корректную дату выдачи.") }
            isValid = false
        }

        // Проверка загрузки фото документов (обязательные)
        if (currentState.driverLicenseImageUri == null) {
            setState { copy(documentsError = "Пожалуйста, загрузите все необходимые фото.") }
            isValid = false
        } else if (currentState.passportImageUri == null) {
            setState { copy(documentsError = "Пожалуйста, загрузите все необходимые фото.") }
            isValid = false
        }

        return isValid
    }

    /**
     * Проверяет корректность формата даты
     * @param dateString строка с датой в формате ДД/ММ/ГГГГ
     * @return true если дата корректна, false в противном случае
     */
    private fun isValidDate(dateString: String): Boolean {
        return try {
            val format = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            format.isLenient = false
            val date = format.parse(dateString)
            date != null && !date.after(Date()) // Дата не должна быть в будущем
        } catch (e: Exception) {
            false
        }
    }
}