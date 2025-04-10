package com.example.drivenext.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.drivenext.domain.model.Car

/**
 * Entity-класс для хранения информации об автомобиле в базе данных
 * @property id Уникальный идентификатор автомобиля
 * @property brand Марка автомобиля
 * @property model Модель автомобиля
 * @property year Год выпуска
 * @property pricePerDay Стоимость аренды за день
 * @property description Описание автомобиля
 * @property imageUrl URL изображения автомобиля
 * @property isAvailable Доступность автомобиля для аренды
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
     * Преобразует entity в domain модель
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
         * Преобразует domain модель в entity
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