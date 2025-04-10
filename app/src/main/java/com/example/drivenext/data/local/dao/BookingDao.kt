package com.example.drivenext.data.local.dao

import androidx.room.*
import com.example.drivenext.data.local.entity.BookingEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * DAO интерфейс для работы с бронированиями в базе данных
 */
@Dao
interface BookingDao {
    // Получение всех бронирований
    @Query("SELECT * FROM bookings")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :bookingId")
    suspend fun getBookingById(bookingId: Long): BookingEntity?

    /**
     * Получает все бронирования пользователя
     * @param userId ID пользователя
     * @return Список бронирований
     */
    @Query("SELECT * FROM bookings WHERE userId = :userId")
    fun getBookingsByUserId(userId: Long): Flow<List<BookingEntity>>

    // Получение бронирований для конкретного автомобиля
    @Query("SELECT * FROM bookings WHERE carId = :carId")
    fun getBookingsByCarId(carId: Long): Flow<List<BookingEntity>>

    // Получить пересекающиеся бронирования для автомобиля
    @Query("SELECT * FROM bookings WHERE carId = :carId AND ((startDate BETWEEN :startDate AND :endDate) OR (endDate BETWEEN :startDate AND :endDate) OR (startDate <= :startDate AND endDate >= :endDate))")
    suspend fun getOverlappingBookings(carId: Long, startDate: Date, endDate: Date): List<BookingEntity>

    /**
     * Вставляет новое бронирование
     * @param booking Новое бронирование
     * @return ID вставленного бронирования
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity): Long

    /**
     * Обновляет существующее бронирование
     * @param booking Обновленное бронирование
     */
    @Update
    suspend fun updateBooking(booking: BookingEntity)

    /**
     * Удаляет бронирование
     * @param booking Бронирование для удаления
     */
    @Delete
    suspend fun deleteBooking(booking: BookingEntity)
}