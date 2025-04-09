package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.Car
import com.example.drivenext.domain.repository.CarRepository
import com.example.drivenext.utils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving available cars
 */
class GetAvailableCarsUseCase @Inject constructor(
    private val carRepository: CarRepository
) {
    operator fun invoke(): Flow<Result<List<Car>>> {
        return carRepository.getAvailableCars()
    }
}