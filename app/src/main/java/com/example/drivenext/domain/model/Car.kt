package com.example.drivenext.domain.model

/**
 * Доменная модель автомобиля
 */
data class Car(
    /** Уникальный идентификатор */
    val id: Long = 0,
    
    /** Марка автомобиля */
    val brand: String,
    
    /** Модель автомобиля */
    val model: String,
    
    /** Год выпуска */
    val year: Int,
    
    /** Стоимость аренды в день */
    val pricePerDay: Double,
    
    /** Описание автомобиля */
    val description: String,
    
    /** URL изображения */
    val imageUrl: String,
    
    /** Доступность для аренды */
    val isAvailable: Boolean = true
)