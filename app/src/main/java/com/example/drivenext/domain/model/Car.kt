package com.example.drivenext.domain.model

/**
 * Domain model representing a car in the application
 */
data class Car(
    val id: Long = 0,
    val brand: String,
    val model: String,
    val year: Int,
    val pricePerDay: Double,
    val description: String,
    val imageUrl: String,
    val isAvailable: Boolean = true
)