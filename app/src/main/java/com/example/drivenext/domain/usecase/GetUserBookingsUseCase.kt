package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.Booking
import com.example.drivenext.domain.repository.BookingRepository
import com.example.drivenext.utils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving a user's bookings
 */
class GetUserBookingsUseCase @Inject constructor(
    private val bookingRepository: BookingRepository
) {
    operator fun invoke(userId: Long): Flow<Result<List<Booking>>> {
        return bookingRepository.getBookingsByUserId(userId)
    }
}