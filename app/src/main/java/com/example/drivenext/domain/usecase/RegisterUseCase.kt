package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.User
import com.example.drivenext.domain.repository.UserRepository
import com.example.drivenext.utils.Result
import javax.inject.Inject

/**
 * Use case for user registration functionality
 */
class RegisterUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(name: String, email: String, phoneNumber: String, password: String): Result<Long> {
        // Validate password
        if (password.length < 6) {
            return Result.Error(Exception("Пароль должен содержать не менее 6 символов"))
        }
        
        if (!password.any { it.isDigit() }) {
            return Result.Error(Exception("Пароль должен содержать хотя бы одну цифру"))
        }
        
        // Check if user already exists
        return when (val existingUser = userRepository.getUserByEmail(email)) {
            is Result.Success -> {
                // User already exists
                Result.Error(Exception("Пользователь с таким email уже существует"))
            }
            is Result.Error -> {
                if (existingUser.exception.message?.contains("User not found") == true) {
                    // User doesn't exist, proceed with registration
                    val newUser = User(
                        name = name,
                        email = email,
                        phoneNumber = phoneNumber,
                        password = password
                    )
                    userRepository.createUser(newUser)
                } else {
                    // Other error occurred
                    Result.Error(Exception("Ошибка при проверке существующих пользователей: ${existingUser.exception.message}"))
                }
            }
            is Result.Loading -> Result.Loading
        }
    }
}