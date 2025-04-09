package com.example.drivenext.domain.model

/**
 * Domain model representing a user in the application
 */
data class User(
    val id: Long = 0,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val password: String
)