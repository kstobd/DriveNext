package com.example.drivenext.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.drivenext.domain.model.Booking
import com.example.drivenext.domain.model.BookingStatus
import java.util.Date

/**
 * Room entity representing a booking in the database
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
    val userId: Long,
    val carId: Long,
    val startDate: Date,
    val endDate: Date,
    val totalPrice: Double,
    val status: String
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