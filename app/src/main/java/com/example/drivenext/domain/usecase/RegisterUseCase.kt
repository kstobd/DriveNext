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
        // Check if user already exists
        return when (val existingUser = userRepository.getUserByEmail(email)) {
            is Result.Success -> {
                // User already exists
                Result.Error(Exception("User with this email already exists"))
            }
            is Result.Error -> {
                // User doesn't exist, proceed with registration
                val newUser = User(
                    name = name,
                    email = email,
                    phoneNumber = phoneNumber,
                    password = password
                )
                userRepository.createUser(newUser)
            }
            is Result.Loading -> Result.Loading
        }
    }
}