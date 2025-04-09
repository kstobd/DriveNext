package com.example.drivenext.data.local

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.drivenext.data.local.entity.CarEntity

/**
 * Utility class for initializing the database with sample data
 */
class DatabaseInitializer {
    
    companion object {
        /**
         * Preloads database with sample cars
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
         * Creates sample car data
         */
        private fun getSampleCars(): List<CarEntity> {
            return listOf(
                CarEntity(
                    brand = "Toyota",
                    model = "Camry",
                    year = 2023,
                    pricePerDay = 65.0,
                    description = "Комфортный семейный седан с экономичным расходом топлива",
                    imageUrl = "https://scene7.toyota.eu/is/image/toyotaeurope/10-03-entdecke-toyota-das-ist-toyota-klassiker-toyota-prius-toyota:Medium-Landscape?ts=0&resMode=sharp2&op_usm=1.75,0.3,2,0",
                    isAvailable = true
                ),
                CarEntity(
                    brand = "Honda",
                    model = "Civic",
                    year = 2022,
                    pricePerDay = 55.0,
                    description = "Компактный автомобиль с отличной управляемостью и экономичностью",
                    imageUrl = "https://www.cnet.com/a/img/resize/4ae70866aa979b5bae8aaae6c46da21c3c2ada0c/hub/2021/11/15/973ad5f0-e23e-45ad-8d89-58665efa7976/2022-honda-civic-sedan-sport-001.jpg",
                    isAvailable = true
                ),
                CarEntity(
                    brand = "BMW",
                    model = "X5",
                    year = 2022,
                    pricePerDay = 120.0,
                    description = "Роскошный спортивный внедорожник с передовыми технологиями",
                    imageUrl = "https://www.topgear.com/sites/default/files/2022/07/P90473042_highRes.jpg",
                    isAvailable = true
                ),
                CarEntity(
                    brand = "Mercedes-Benz",
                    model = "E-Class",
                    year = 2023,
                    pricePerDay = 110.0,
                    description = "Элегантный бизнес-седан с комфортным салоном и плавным ходом",
                    imageUrl = "https://www.topgear.com/sites/default/files/2021/11/6-%20Mercedes-AMG%20E63%20S%20Estate.jpg",
                    isAvailable = true
                ),
                CarEntity(
                    brand = "Audi",
                    model = "Q7",
                    year = 2022,
                    pricePerDay = 115.0,
                    description = "Просторный и комфортабельный внедорожник премиум-класса",
                    imageUrl = "https://www.topgear.com/sites/default/files/images/cars-road-test/2019/11/16b8e6f39ab689a0e1d7de0cb56a967d/audi_q7_e-tron_2019_006.jpg",
                    isAvailable = true
                ),
                CarEntity(
                    brand = "Volkswagen",
                    model = "Golf",
                    year = 2023,
                    pricePerDay = 50.0,
                    description = "Экономичный и практичный хэтчбек с отличной управляемостью",
                    imageUrl = "https://cdn.motor1.com/images/mgl/MkJpQ/s1/2021-volkswagen-golf-gti-clubsport-45.jpg",
                    isAvailable = true
                ),
                CarEntity(
                    brand = "Tesla",
                    model = "Model 3",
                    year = 2023,
                    pricePerDay = 95.0,
                    description = "Полностью электрический спортивный седан с впечатляющим запасом хода",
                    imageUrl = "https://www.topgear.com/sites/default/files/cars-car/image/2021/05/tesla_model_3_blue.jpg",
                    isAvailable = true
                )
            )
        }
    }
}