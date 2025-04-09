package com.example.drivenext.domain.model

import java.util.Date
import com.example.drivenext.domain.model.BookingStatus

/**
 * Domain model representing a booking in the application
 */
data class Booking(
    val id: Long = 0,
    val userId: Long,
    val carId: Long,
    val startDate: Date,
    val endDate: Date,
    val totalPrice: Double,
    val status: BookingStatus = BookingStatus.PENDING
)