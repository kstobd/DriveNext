package com.example.drivenext.data.mappers

import com.example.drivenext.data.local.entity.BookingEntity
import com.example.drivenext.data.local.entity.CarEntity
import com.example.drivenext.data.local.entity.UserEntity
import com.example.drivenext.domain.model.Booking
import com.example.drivenext.domain.model.BookingStatus
import com.example.drivenext.domain.model.Car
import com.example.drivenext.domain.model.User

/**
 * Класс-маппер для конвертации между Entity и Domain моделями
 */
object EntityMappers {
    
    // Мапперы для User (Пользователя)
    fun mapUserEntityToDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            name = entity.name,
            email = entity.email,
            phoneNumber = entity.phoneNumber,
            password = entity.password,
            firstName = entity.firstName,
            lastName = entity.lastName,
            middleName = entity.middleName,
            birthDate = entity.birthDate,
            gender = entity.gender
        )
    }
    
    fun mapUserDomainToEntity(domain: User): UserEntity {
        // Используем метод fromDomainModel который правильно хеширует пароль
        return UserEntity.fromDomainModel(domain)
    }
    
    // Мапперы для Car (Автомобиля)
    fun mapCarEntityToDomain(entity: CarEntity): Car {
        return Car(
            id = entity.id,
            model = entity.model,
            brand = entity.brand,
            imageUrl = entity.imageUrl,
            year = entity.year,
            pricePerDay = entity.pricePerDay,
            description = entity.description,
            isAvailable = entity.isAvailable
        )
    }
    
    fun mapCarDomainToEntity(domain: Car): CarEntity {
        return CarEntity(
            id = domain.id,
            model = domain.model,
            brand = domain.brand,
            imageUrl = domain.imageUrl,
            year = domain.year,
            pricePerDay = domain.pricePerDay,
            description = domain.description,
            isAvailable = domain.isAvailable
        )
    }
    
    // Мапперы для Booking (Бронирования)
    fun mapBookingEntityToDomain(entity: BookingEntity): Booking {
        return Booking(
            id = entity.id,
            userId = entity.userId,
            carId = entity.carId,
            startDate = entity.startDate,
            endDate = entity.endDate,
            totalPrice = entity.totalPrice,
            status = BookingStatus.valueOf(entity.status)
        )
    }
    
    fun mapBookingDomainToEntity(domain: Booking): BookingEntity {
        return BookingEntity(
            id = domain.id,
            userId = domain.userId,
            carId = domain.carId,
            startDate = domain.startDate,
            endDate = domain.endDate,
            totalPrice = domain.totalPrice,
            status = domain.status.name
        )
    }
}