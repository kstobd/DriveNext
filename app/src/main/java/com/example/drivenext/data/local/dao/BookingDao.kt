package com.example.drivenext.data.local.dao

import androidx.room.*
import com.example.drivenext.data.local.entity.BookingEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * DAO for Booking operations in the database
 */
@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :bookingId")
    suspend fun getBookingById(bookingId: Long): BookingEntity?

    @Query("SELECT * FROM bookings WHERE userId = :userId")
    fun getBookingsByUserId(userId: Long): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE carId = :carId")
    fun getBookingsByCarId(carId: Long): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE carId = :carId AND ((startDate BETWEEN :startDate AND :endDate) OR (endDate BETWEEN :startDate AND :endDate) OR (startDate <= :startDate AND endDate >= :endDate))")
    suspend fun getOverlappingBookings(carId: Long, startDate: Date, endDate: Date): List<BookingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity): Long

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    @Delete
    suspend fun deleteBooking(booking: BookingEntity)
}