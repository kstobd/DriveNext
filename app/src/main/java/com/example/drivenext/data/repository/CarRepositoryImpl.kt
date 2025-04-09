package com.example.drivenext.data.repository

import com.example.drivenext.data.local.dao.CarDao
import com.example.drivenext.data.mappers.EntityMappers
import com.example.drivenext.domain.model.Car
import com.example.drivenext.domain.repository.CarRepository
import com.example.drivenext.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of CarRepository using Room database
 */
class CarRepositoryImpl @Inject constructor(
    private val carDao: CarDao
) : CarRepository {

    override fun getAllCars(): Flow<Result<List<Car>>> {
        return carDao.getAllCars()
            .catch { e -> 
                emit(emptyList())
                e.printStackTrace()
            }
            .map { entities ->
                try {
                    Result.Success(entities.map { EntityMappers.mapCarEntityToDomain(it) })
                } catch (e: Exception) {
                    e.printStackTrace()
                    Result.Error(e)
                }
            }
            .catch { e ->
                e.printStackTrace()
                emit(Result.Error(Exception(e.message, e)))
            }
    }

    override fun getAvailableCars(): Flow<Result<List<Car>>> {
        return carDao.getAvailableCars()
            .catch { e -> 
                emit(emptyList())
                e.printStackTrace()
            }
            .map { entities ->
                try {
                    Result.Success(entities.map { EntityMappers.mapCarEntityToDomain(it) })
                } catch (e: Exception) {
                    e.printStackTrace()
                    Result.Error(e)
                }
            }
            .catch { e ->
                e.printStackTrace()
                emit(Result.Error(Exception(e.message, e)))
            }
    }

    override suspend fun getCarById(carId: Long): Result<Car> {
        return try {
            val carEntity = carDao.getCarById(carId)
            if (carEntity != null) {
                Result.Success(EntityMappers.mapCarEntityToDomain(carEntity))
            } else {
                Result.Error(Exception("Car not found"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    override suspend fun createCar(car: Car): Result<Long> {
        return try {
            val id = carDao.insertCar(EntityMappers.mapCarDomainToEntity(car))
            Result.Success(id)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    override suspend fun updateCar(car: Car): Result<Unit> {
        return try {
            carDao.updateCar(EntityMappers.mapCarDomainToEntity(car))
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    override suspend fun deleteCar(car: Car): Result<Unit> {
        return try {
            carDao.deleteCar(EntityMappers.mapCarDomainToEntity(car))
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }
}