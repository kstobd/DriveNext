package com.example.drivenext.domain.repository

import com.example.drivenext.domain.model.Car
import com.example.drivenext.utils.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for handling car data operations
 */
interface CarRepository {
    suspend fun getCarById(id: Long): Result<Car>
    suspend fun createCar(car: Car): Result<Long>
    suspend fun updateCar(car: Car): Result<Unit>
    suspend fun deleteCar(car: Car): Result<Unit>
    fun getAllCars(): Flow<Result<List<Car>>>
    fun getAvailableCars(): Flow<Result<List<Car>>>
}