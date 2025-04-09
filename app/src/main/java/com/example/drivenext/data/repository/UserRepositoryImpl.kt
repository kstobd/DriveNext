package com.example.drivenext.data.repository

import com.example.drivenext.data.local.dao.UserDao
import com.example.drivenext.data.mappers.EntityMappers
import com.example.drivenext.domain.model.User
import com.example.drivenext.domain.repository.UserRepository
import com.example.drivenext.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of UserRepository using Room database
 */
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override fun getAllUsers(): Flow<Result<List<User>>> {
        return userDao.getAllUsers().map { entities ->
            try {
                Result.Success(entities.map { EntityMappers.mapUserEntityToDomain(it) })
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun getUserById(userId: Long): Result<User> {
        return try {
            val userEntity = userDao.getUserById(userId)
            if (userEntity != null) {
                Result.Success(EntityMappers.mapUserEntityToDomain(userEntity))
            } else {
                Result.Error(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getUserByEmail(email: String): Result<User> {
        return try {
            val userEntity = userDao.getUserByEmail(email)
            if (userEntity != null) {
                Result.Success(EntityMappers.mapUserEntityToDomain(userEntity))
            } else {
                Result.Error(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun createUser(user: User): Result<Long> {
        return try {
            val id = userDao.insertUser(EntityMappers.mapUserDomainToEntity(user))
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return try {
            userDao.updateUser(EntityMappers.mapUserDomainToEntity(user))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteUser(user: User): Result<Unit> {
        return try {
            userDao.deleteUser(EntityMappers.mapUserDomainToEntity(user))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val user = userDao.getUserByEmailAndPassword(email, password)
            if (user != null) {
                Result.Success(EntityMappers.mapUserEntityToDomain(user))
            } else {
                Result.Error(Exception("Invalid email or password"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}