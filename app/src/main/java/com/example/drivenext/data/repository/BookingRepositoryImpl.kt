package com.example.drivenext.data.repository

import com.example.drivenext.data.local.dao.BookingDao
import com.example.drivenext.data.mappers.EntityMappers
import com.example.drivenext.domain.model.Booking
import com.example.drivenext.domain.repository.BookingRepository
import com.example.drivenext.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

/**
 * Implementation of BookingRepository using Room database
 */
class BookingRepositoryImpl @Inject constructor(
    private val bookingDao: BookingDao
) : BookingRepository {

    override fun getAllBookings(): Flow<Result<List<Booking>>> {
        return bookingDao.getAllBookings().map { entities ->
            try {
                Result.Success(entities.map { EntityMappers.mapBookingEntityToDomain(it) })
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override fun getBookingsByUserId(userId: Long): Flow<Result<List<Booking>>> {
        return bookingDao.getBookingsByUserId(userId).map { entities ->
            try {
                Result.Success(entities.map { EntityMappers.mapBookingEntityToDomain(it) })
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override fun getBookingsByCarId(carId: Long): Flow<Result<List<Booking>>> {
        return bookingDao.getBookingsByCarId(carId).map { entities ->
            try {
                Result.Success(entities.map { EntityMappers.mapBookingEntityToDomain(it) })
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun getBookingById(bookingId: Long): Result<Booking> {
        return try {
            val bookingEntity = bookingDao.getBookingById(bookingId)
            if (bookingEntity != null) {
                Result.Success(EntityMappers.mapBookingEntityToDomain(bookingEntity))
            } else {
                Result.Error(Exception("Booking not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun checkCarAvailability(carId: Long, startDate: Date, endDate: Date): Result<Boolean> {
        return try {
            val overlappingBookings = bookingDao.getOverlappingBookings(carId, startDate, endDate)
            Result.Success(overlappingBookings.isEmpty())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun createBooking(booking: Booking): Result<Long> {
        return try {
            val id = bookingDao.insertBooking(EntityMappers.mapBookingDomainToEntity(booking))
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateBooking(booking: Booking): Result<Unit> {
        return try {
            bookingDao.updateBooking(EntityMappers.mapBookingDomainToEntity(booking))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteBooking(booking: Booking): Result<Unit> {
        return try {
            bookingDao.deleteBooking(EntityMappers.mapBookingDomainToEntity(booking))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}