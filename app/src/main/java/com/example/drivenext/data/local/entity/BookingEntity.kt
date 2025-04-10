package com.example.drivenext.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.drivenext.domain.model.Booking
import com.example.drivenext.domain.model.BookingStatus
import java.util.Date

/**
 * Entity-класс для хранения информации о бронировании в базе данных
 * @property id Уникальный идентификатор бронирования
 * @property userId ID пользователя, создавшего бронирование
 * @property carId ID забронированного автомобиля
 * @property startDate Дата начала аренды
 * @property endDate Дата окончания аренды
 * @property totalPrice Общая стоимость аренды
 * @property status Статус бронирования (PENDING, CONFIRMED, CANCELLED, COMPLETED)
 */
@Entity(
    tableName = "bookings",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CarEntity::class,
            parentColumns = ["id"],
            childColumns = ["carId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("userId"),
        Index("carId")
    ]
)
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // ID пользователя, сделавшего бронирование
    val userId: Long,
    
    // ID забронированного автомобиля
    val carId: Long,
    
    // Дата начала аренды
    val startDate: Date,
    
    // Дата окончания аренды
    val endDate: Date,
    
    // Общая стоимость аренды
    val totalPrice: Double,
    
    // Статус бронирования
    val status: String // Строковое представление enum BookingStatus
) {
    /**
     * Convert entity to domain model
     */
    fun toDomainModel(): Booking {
        return Booking(
            id = id,
            userId = userId,
            carId = carId,
            startDate = startDate,
            endDate = endDate,
            totalPrice = totalPrice,
            status = BookingStatus.valueOf(status)
        )
    }

    companion object {
        /**
         * Convert domain model to entity
         */
        fun fromDomainModel(booking: Booking): BookingEntity {
            return BookingEntity(
                id = booking.id,
                userId = booking.userId,
                carId = booking.carId,
                startDate = booking.startDate,
                endDate = booking.endDate,
                totalPrice = booking.totalPrice,
                status = booking.status.name
            )
        }
    }
}