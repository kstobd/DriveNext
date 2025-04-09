package com.example.drivenext.domain.repository

import com.example.drivenext.domain.model.Booking
import com.example.drivenext.utils.Result
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Repository interface for handling booking data operations
 */
interface BookingRepository {
    suspend fun getBookingById(id: Long): Result<Booking>
    suspend fun createBooking(booking: Booking): Result<Long>
    suspend fun updateBooking(booking: Booking): Result<Unit>
    suspend fun deleteBooking(booking: Booking): Result<Unit>
    fun getAllBookings(): Flow<Result<List<Booking>>>
    fun getBookingsByUserId(userId: Long): Flow<Result<List<Booking>>>
    fun getBookingsByCarId(carId: Long): Flow<Result<List<Booking>>>
    suspend fun checkCarAvailability(carId: Long, startDate: Date, endDate: Date): Result<Boolean>
}