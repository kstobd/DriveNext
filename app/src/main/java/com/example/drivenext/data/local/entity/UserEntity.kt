package com.example.drivenext.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.drivenext.domain.model.User
import com.example.drivenext.util.PasswordUtils
import java.util.Date

/**
 * Room entity representing a user in the database
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val firstName: String = "",
    val lastName: String = "",
    val middleName: String = "",
    val birthDate: Date? = null,
    val gender: String = ""
) {
    /**
     * Convert entity to domain model
     */
    fun toDomainModel(): User {
        return User(
            id = id,
            name = name,
            email = email,
            phoneNumber = phoneNumber,
            password = password,
            firstName = firstName,
            lastName = lastName,
            middleName = middleName,
            birthDate = birthDate,
            gender = gender
        )
    }

    companion object {
        /**
         * Convert domain model to entity
         */
        fun fromDomainModel(user: User): UserEntity {
            return UserEntity(
                id = user.id,
                name = user.name,
                email = user.email,
                phoneNumber = user.phoneNumber,
                password = if (isPasswordHashed(user.password)) user.password else PasswordUtils.hashPassword(user.password),
                firstName = user.firstName,
                lastName = user.lastName,
                middleName = user.middleName,
                birthDate = user.birthDate,
                gender = user.gender
            )
        }
        
        /**
         * Проверяет, хеширован ли уже пароль
         */
        private fun isPasswordHashed(password: String): Boolean {
            return password.contains(":") && password.split(":").size == 2
        }
    }
}