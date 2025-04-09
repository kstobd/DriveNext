package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.Booking
import com.example.drivenext.domain.model.BookingStatus
import com.example.drivenext.domain.repository.BookingRepository
import com.example.drivenext.domain.repository.CarRepository
import com.example.drivenext.utils.Result
import java.util.Date
import javax.inject.Inject

/**
 * Use case for creating a new car booking
 */
class CreateBookingUseCase @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val carRepository: CarRepository
) {
    suspend operator fun invoke(
        userId: Long,
        carId: Long,
        startDate: Date,
        endDate: Date
    ): Result<Long> {
        // Check if the car exists
        val carResult = carRepository.getCarById(carId)
        if (carResult is Result.Error) {
            return Result.Error(Exception("Car not found"))
        }

        // Check if the car is available for the requested dates
        val availabilityResult = bookingRepository.checkCarAvailability(carId, startDate, endDate)
        if (availabilityResult is Result.Error) {
            return Result.Error(Exception("Failed to check car availability"))
        }

        if (availabilityResult is Result.Success && !availabilityResult.data) {
            return Result.Error(Exception("Car is not available for the selected dates"))
        }

        // Calculate total price
        val car = (carResult as Result.Success).data
        val durationInMillis = endDate.time - startDate.time
        val durationInDays = (durationInMillis / (1000 * 60 * 60 * 24)).toInt() + 1
        val totalPrice = car.pricePerDay * durationInDays

        // Create booking
        val booking = Booking(
            userId = userId,
            carId = carId,
            startDate = startDate,
            endDate = endDate,
            totalPrice = totalPrice,
            status = BookingStatus.PENDING
        )

        return bookingRepository.createBooking(booking)
    }
}