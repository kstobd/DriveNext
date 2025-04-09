package com.example.drivenext.domain.repository

import com.example.drivenext.domain.model.User
import com.example.drivenext.utils.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for handling user data operations
 */
interface UserRepository {
    suspend fun getUserById(id: Long): Result<User>
    suspend fun getUserByEmail(email: String): Result<User>
    suspend fun createUser(user: User): Result<Long>
    suspend fun updateUser(user: User): Result<Unit>
    suspend fun deleteUser(user: User): Result<Unit>
    fun getAllUsers(): Flow<Result<List<User>>>
    suspend fun login(email: String, password: String): Result<User>
}