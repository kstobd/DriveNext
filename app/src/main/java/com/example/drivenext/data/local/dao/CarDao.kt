package com.example.drivenext.data.local.dao

import androidx.room.*
import com.example.drivenext.data.local.entity.CarEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO интерфейс для работы с автомобилями в базе данных
 */
@Dao
interface CarDao {
    /**
     * Получает все автомобили
     * @return Поток списка автомобилей
     */
    @Query("SELECT * FROM cars")
    fun getAllCars(): Flow<List<CarEntity>>

    /**
     * Получает список доступных автомобилей
     * @return Поток списка доступных автомобилей
     */
    @Query("SELECT * FROM cars WHERE isAvailable = 1")
    fun getAvailableCars(): Flow<List<CarEntity>>

    /**
     * Получает автомобиль по ID
     * @param carId ID автомобиля
     * @return Автомобиль или null
     */
    @Query("SELECT * FROM cars WHERE id = :carId")
    suspend fun getCarById(carId: Long): CarEntity?

    /**
     * Получает количество автомобилей в базе
     * @return Количество автомобилей
     */
    @Query("SELECT COUNT(*) FROM cars")
    suspend fun getCarsCount(): Int

    /**
     * Вставляет новый автомобиль
     * @param car Новый автомобиль
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: CarEntity): Long

    /**
     * Вставляет список автомобилей
     * @param cars Список автомобилей
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCars(cars: List<CarEntity>)

    /**
     * Обновляет существующий автомобиль
     * @param car Обновленный автомобиль
     */
    @Update
    suspend fun updateCar(car: CarEntity)

    /**
     * Удаляет автомобиль
     * @param car Автомобиль для удаления
     */
    @Delete
    suspend fun deleteCar(car: CarEntity)
}