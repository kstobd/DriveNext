package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.User
import com.example.drivenext.domain.repository.UserRepository
import com.example.drivenext.utils.Result
import javax.inject.Inject

/**
 * Use case for user login functionality
 */
class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        // Валидация входных данных
        if (email.isBlank()) {
            return Result.Error(Exception("Email не может быть пустым"))
        }
        
        if (password.isBlank()) {
            return Result.Error(Exception("Пароль не может быть пустым"))
        }
        
        // Get user by email
        return when (val result = userRepository.getUserByEmail(email)) {
            is Result.Success -> {
                // Check if password matches
                if (result.data.password == password) {
                    result
                } else {
                    Result.Error(Exception("Неверный пароль"))
                }
            }
            is Result.Error -> {
                if (result.exception.message?.contains("User not found") == true) {
                    Result.Error(Exception("Пользователь с таким email не существует"))
                } else {
                    Result.Error(Exception("Ошибка при авторизации: ${result.exception.message}"))
                }
            }
            is Result.Loading -> Result.Loading
        }
    }
}