package com.example.drivenext.data.local

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.drivenext.data.local.entity.CarEntity

/**
 * Вспомогательный класс для инициализации базы данных тестовыми данными
 */
class DatabaseInitializer {
    
    companion object {
        /**
         * Предварительная загрузка базы данных тестовыми автомобилями.
         * База заполняется только если она пустая.
         * @param database Экземпляр базы данных для заполнения
         */
        fun preloadDatabase(database: AppDatabase) {
            val carDao = database.carDao()
            
            CoroutineScope(Dispatchers.IO).launch {
                // Проверяем, есть ли уже данные в базе
                val carsCount = carDao.getCarsCount()
                
                // Если данных еще нет, добавляем тестовые данные
                if (carsCount == 0) {
                    val cars = getSampleCars()
                    cars.forEach { car ->
                        carDao.insertCar(car)
                    }
                }
            }
        }
        
        /**
         * Создаёт список тестовых автомобилей для предварительной загрузки
         * @return Список тестовых автомобилей
         */
        private fun getSampleCars(): List<CarEntity> {
            return listOf(
                CarEntity(
                    brand = "Toyota",
                    model = "Camry",
                    year = 2023,
                    pricePerDay = 65.0,
                    description = "Комфортный седан с отличной экономией топлива и надежностью",
                    imageUrl = "https://orag-vehicle-images.s3.us-west-2.amazonaws.com/2023/Toyota/Camry/cc_2023TOC020004_01_1280_01J9.png",
                    isAvailable = true
                ),
                CarEntity(
                    brand = "BMW",
                    model = "M5",
                    year = 2022,
                    pricePerDay = 120.0,
                    description = "Спортивный седан с мощным двигателем и роскошным интерьером",
                    imageUrl = "https://inv.assets.sincrod.com/ChromeColorMatch/us/TRANSPARENT_cc_2023BMC170003_01_1280_475.png",
                    isAvailable = true
                ),
                CarEntity(
                    brand = "Audi",
                    model = "Q8 E-tron",
                    year = 2023,
                    pricePerDay = 115.0,
                    description = "Электрический спортивный кроссовер с элегантным дизайном и высокой производительностью",
                    imageUrl = "https://platform.cstatic-images.com/in/v2/stock_photos/461d4a97-cb49-4c9c-abf8-015314e7f423/d124cfca-b0c6-488e-9549-90480021c11f.png",
                    isAvailable = true
                ),
                CarEntity(
                    brand = "Volkswagen",
                    model = "Golf",
                    year = 2023,
                    pricePerDay = 50.0,
                    description = "Экономичный и практичный хэтчбек с отличной управляемостью",
                    imageUrl = "https://images-stag.jazelc.com/uploads/galpinvolkswagen-m2en/2023-Volkswagen-Golf-GTI.png",
                    isAvailable = true
                ),
                CarEntity(
                    brand = "Tesla",
                    model = "Model 3",
                    year = 2023,
                    pricePerDay = 95.0,
                    description = "Полностью электрический спортивный седан с впечатляющим запасом хода",
                    imageUrl = "https://assets.zappyride.com/img/vehicles/chromestyle/441106/style-set-1280/2023TSC030017_1280_01.png",
                    isAvailable = true
                )
            )
        }
    }
}