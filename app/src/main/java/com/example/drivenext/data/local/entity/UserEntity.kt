package com.example.drivenext.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.drivenext.domain.model.User
import com.example.drivenext.util.PasswordUtils
import java.util.Date

/**
 * Entity-класс для хранения информации о пользователе в базе данных
 * @property id Уникальный идентификатор пользователя
 * @property name Имя пользователя для входа в систему
 * @property email Email пользователя
 * @property phoneNumber Номер телефона
 * @property password Хэшированный пароль
 * @property firstName Имя
 * @property lastName Фамилия
 * @property middleName Отчество
 * @property birthDate Дата рождения
 * @property gender Пол пользователя
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Имя пользователя
    val name: String,
    
    // Электронная почта
    val email: String,
    val phoneNumber: String,
    val password: String, // Хранится в хэшированном виде
    val firstName: String = "",
    val lastName: String = "",
    val middleName: String = "",
    val birthDate: Date? = null,
    val gender: String = ""
) {
    /**
     * Преобразует entity в domain модель
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
         * Создает entity из domain модели, хэширует пароль
         */
        fun fromDomainModel(user: User): UserEntity {
            return UserEntity(
                id = user.id,
                name = user.name,
                email = user.email,
                phoneNumber = user.phoneNumber,
                password = user.password, // Пароль уже должен быть хеширован на уровне VM
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