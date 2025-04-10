package com.example.drivenext.data.local.dao

import androidx.room.*
import com.example.drivenext.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO интерфейс для работы с пользователями в базе данных
 */
@Dao
interface UserDao {
    /**
     * Получает всех пользователей
     * @return Список пользователей
     */
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    /**
     * Получает пользователя по ID
     * @param userId ID пользователя
     * @return Пользователь или null
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): UserEntity?

    /**
     * Получает пользователя по email
     * @param email Email пользователя
     * @return Пользователь или null
     */
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun getUserByEmailAndPassword(email: String, password: String): UserEntity?

    /**
     * Вставляет нового пользователя
     * @param user Новый пользователь
     * @return ID вставленного пользователя
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    /**
     * Обновляет существующего пользователя
     * @param user Обновленный пользователь
     */
    @Update
    suspend fun updateUser(user: UserEntity)

    /**
     * Удаляет пользователя
     * @param user Пользователь для удаления
     */
    @Delete
    suspend fun deleteUser(user: UserEntity)
}