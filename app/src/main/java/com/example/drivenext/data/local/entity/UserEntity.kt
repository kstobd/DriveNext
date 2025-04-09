package com.example.drivenext.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.drivenext.domain.model.User

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
    val password: String
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
            password = password
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
                password = user.password
            )
        }
    }
}