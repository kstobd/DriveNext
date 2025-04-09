package com.example.drivenext.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.drivenext.domain.model.Car

/**
 * Room entity representing a car in the database
 */
@Entity(tableName = "cars")
data class CarEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val brand: String,
    val model: String,
    val year: Int,
    val pricePerDay: Double,
    val description: String,
    val imageUrl: String,
    val isAvailable: Boolean
) {
    /**
     * Convert entity to domain model
     */
    fun toDomainModel(): Car {
        return Car(
            id = id,
            brand = brand,
            model = model,
            year = year,
            pricePerDay = pricePerDay,
            description = description,
            imageUrl = imageUrl,
            isAvailable = isAvailable
        )
    }

    companion object {
        /**
         * Convert domain model to entity
         */
        fun fromDomainModel(car: Car): CarEntity {
            return CarEntity(
                id = car.id,
                brand = car.brand,
                model = car.model,
                year = car.year,
                pricePerDay = car.pricePerDay,
                description = car.description,
                imageUrl = car.imageUrl,
                isAvailable = car.isAvailable
            )
        }
    }
}