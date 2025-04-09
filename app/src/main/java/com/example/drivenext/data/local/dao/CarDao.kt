package com.example.drivenext.data.local.dao

import androidx.room.*
import com.example.drivenext.data.local.entity.CarEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Car operations in the database
 */
@Dao
interface CarDao {
    @Query("SELECT * FROM cars")
    fun getAllCars(): Flow<List<CarEntity>>

    @Query("SELECT * FROM cars WHERE isAvailable = 1")
    fun getAvailableCars(): Flow<List<CarEntity>>

    @Query("SELECT * FROM cars WHERE id = :carId")
    suspend fun getCarById(carId: Long): CarEntity?

    @Query("SELECT COUNT(*) FROM cars")
    suspend fun getCarsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: CarEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCars(cars: List<CarEntity>)

    @Update
    suspend fun updateCar(car: CarEntity)

    @Delete
    suspend fun deleteCar(car: CarEntity)
}