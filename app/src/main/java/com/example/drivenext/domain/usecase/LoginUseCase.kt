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
        // Get user by email
        return when (val result = userRepository.getUserByEmail(email)) {
            is Result.Success -> {
                // Check if password matches
                if (result.data.password == password) {
                    result
                } else {
                    Result.Error(Exception("Invalid credentials"))
                }
            }
            is Result.Error -> Result.Error(Exception("Invalid credentials"))
            is Result.Loading -> Result.Loading
        }
    }
}