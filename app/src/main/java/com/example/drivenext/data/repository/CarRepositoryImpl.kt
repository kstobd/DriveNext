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
 * Реализация репозитория для работы с данными автомобилей через Room Database.
 * Обеспечивает:
 * - Преобразование между Entity и Domain моделями
 * - Обработку ошибок при работе с базой данных
 * - Реактивное обновление данных через Flow
 */
class CarRepositoryImpl @Inject constructor(
    private val carDao: CarDao
) : CarRepository {

    /**
     * Получает список всех автомобилей из базы данных.
     * Преобразует сущности в доменные модели и оборачивает результат в Flow.
     * Обрабатывает возможные ошибки и возвращает пустой список в случае проблем.
     */
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

    /**
     * Получает список только доступных для бронирования автомобилей.
     * Фильтрация происходит на уровне SQL-запроса через Room.
     * Обрабатывает ошибки аналогично getAllCars().
     */
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

    /**
     * Получает информацию об одном автомобиле по его ID.
     * В случае отсутствия автомобиля возвращает специальную ошибку "Car not found".
     */
    override suspend fun getCarById(carId: Long): Result<Car> {
        return try {
            val carEntity = carDao.getCarById(carId)
            if (carEntity != null) {
                Result.Success(EntityMappers.mapCarEntityToDomain(carEntity))
            } else {
                Result.Error(Exception("Автомобиль не найден"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    /**
     * Создает новую запись об автомобиле в базе данных.
     * Возвращает ID созданной записи в случае успеха.
     */
    override suspend fun createCar(car: Car): Result<Long> {
        return try {
            val carEntity = EntityMappers.mapCarDomainToEntity(car)
            val id = carDao.insertCar(carEntity)
            Result.Success(id)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    /**
     * Обновляет существующую запись об автомобиле.
     * Предполагает, что автомобиль с таким ID уже существует в базе.
     */
    override suspend fun updateCar(car: Car): Result<Unit> {
        return try {
            val carEntity = EntityMappers.mapCarDomainToEntity(car)
            carDao.updateCar(carEntity)
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }

    /**
     * Удаляет запись об автомобиле из базы данных.
     * В случае отсутствия записи операция считается успешной.
     */
    override suspend fun deleteCar(car: Car): Result<Unit> {
        return try {
            val carEntity = EntityMappers.mapCarDomainToEntity(car)
            carDao.deleteCar(carEntity)
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(e)
        }
    }
}