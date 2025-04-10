package com.example.drivenext.domain.repository

import com.example.drivenext.domain.model.Car
import com.example.drivenext.utils.Result
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий для работы с данными автомобилей.
 * Определяет основные операции для:
 * - Получения информации об автомобилях
 * - Создания новых записей об автомобилях
 * - Обновления существующих записей
 * - Удаления автомобилей из системы
 */
interface CarRepository {
    /**
     * Получает информацию об автомобиле по его идентификатору
     * @param id Уникальный идентификатор автомобиля
     * @return Result с данными автомобиля в случае успеха или ошибкой
     */
    suspend fun getCarById(id: Long): Result<Car>

    /**
     * Создает новую запись об автомобиле
     * @param car Объект с данными нового автомобиля
     * @return Result с идентификатором созданной записи в случае успеха или ошибкой
     */
    suspend fun createCar(car: Car): Result<Long>

    /**
     * Обновляет информацию об существующем автомобиле
     * @param car Объект с обновленными данными автомобиля
     * @return Result с результатом операции
     */
    suspend fun updateCar(car: Car): Result<Unit>

    /**
     * Удаляет информацию об автомобиле
     * @param car Объект автомобиля для удаления
     * @return Result с результатом операции
     */
    suspend fun deleteCar(car: Car): Result<Unit>

    /**
     * Получает поток со списком всех автомобилей в системе
     * @return Flow c Result, содержащим список автомобилей
     */
    fun getAllCars(): Flow<Result<List<Car>>>

    /**
     * Получает поток со списком только доступных для бронирования автомобилей
     * @return Flow c Result, содержащим список доступных автомобилей
     */
    fun getAvailableCars(): Flow<Result<List<Car>>>
}