package com.example.drivenext.domain.model

import java.util.Date

/**
 * Domain model representing a user in the application
 */
data class User(
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
)