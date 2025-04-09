package com.example.drivenext.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.drivenext.domain.model.Car
import com.example.drivenext.domain.repository.CarRepository
import com.example.drivenext.domain.usecase.CreateBookingUseCase
import com.example.drivenext.utils.Result
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel for car detail screen that handles displaying car details and booking
 */
class CarDetailViewModel @Inject constructor(
    private val carRepository: CarRepository,
    private val createBookingUseCase: CreateBookingUseCase
) : BaseViewModel<CarDetailViewModel.CarDetailState, CarDetailViewModel.CarDetailEvent, CarDetailViewModel.CarDetailEffect>() {

    data class CarDetailState(
        val car: Car? = null,
        val startDate: Date? = null,
        val endDate: Date? = null,
        val totalPrice: Double = 0.0,
        val isLoading: Boolean = false,
        val bookingInProgress: Boolean = false,
        val dateError: String? = null
    )

    sealed class CarDetailEvent {
        data class LoadCar(val carId: Long) : CarDetailEvent()
        data class StartDateSelected(val date: Date) : CarDetailEvent()
        data class EndDateSelected(val date: Date) : CarDetailEvent()
        data class BookCar(val userId: Long) : CarDetailEvent()
        object BackPressed : CarDetailEvent()
    }

    sealed class CarDetailEffect {
        object NavigateBack : CarDetailEffect()
        data class ShowError(val message: String) : CarDetailEffect()
        data class BookingSuccess(val bookingId: Long) : CarDetailEffect()
    }

    override fun createInitialState(): CarDetailState = CarDetailState(isLoading = true)

    override fun handleEvent(event: CarDetailEvent) {
        when (event) {
            is CarDetailEvent.LoadCar -> loadCar(event.carId)
            is CarDetailEvent.StartDateSelected -> updateStartDate(event.date)
            is CarDetailEvent.EndDateSelected -> updateEndDate(event.date)
            is CarDetailEvent.BookCar -> bookCar(event.userId)
            is CarDetailEvent.BackPressed -> navigateBack()
        }
    }

    private fun loadCar(carId: Long) {
        setState { copy(isLoading = true) }
        
        viewModelScope.launch {
            when (val result = carRepository.getCarById(carId)) {
                is Result.Success -> {
                    setState { copy(car = result.data, isLoading = false) }
                }
                is Result.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect(CarDetailEffect.ShowError(result.exception.message ?: "Failed to load car details"))
                    setEffect(CarDetailEffect.NavigateBack)
                }
                is Result.Loading -> {
                    setState { copy(isLoading = true) }
                }
            }
        }
    }

    private fun updateStartDate(date: Date) {
        setState { 
            val startDate = date
            val endDate = this.endDate
            val car = this.car
            
            // Calculate total price if both dates are set
            val totalPrice = if (car != null && endDate != null) {
                calculateTotalPrice(car, startDate, endDate)
            } else {
                0.0
            }
            
            copy(
                startDate = startDate, 
                totalPrice = totalPrice, 
                dateError = validateDates(startDate, endDate)
            ) 
        }
    }

    private fun updateEndDate(date: Date) {
        setState { 
            val startDate = this.startDate
            val endDate = date
            val car = this.car
            
            // Calculate total price if both dates are set
            val totalPrice = if (car != null && startDate != null) {
                calculateTotalPrice(car, startDate, endDate)
            } else {
                0.0
            }
            
            copy(
                endDate = endDate, 
                totalPrice = totalPrice, 
                dateError = validateDates(startDate, endDate)
            ) 
        }
    }

    private fun calculateTotalPrice(car: Car, startDate: Date, endDate: Date): Double {
        val durationInMillis = endDate.time - startDate.time
        val durationInDays = (durationInMillis / (1000 * 60 * 60 * 24)).toInt() + 1
        return car.pricePerDay * durationInDays
    }

    private fun validateDates(startDate: Date?, endDate: Date?): String? {
        if (startDate == null || endDate == null) {
            return null
        }
        
        if (startDate.after(endDate)) {
            return "Start date cannot be after end date"
        }
        
        if (startDate.before(Date())) {
            return "Start date cannot be in the past"
        }
        
        return null
    }

    private fun bookCar(userId: Long) {
        val car = state.value.car
        val startDate = state.value.startDate
        val endDate = state.value.endDate
        
        if (car == null || startDate == null || endDate == null) {
            setEffect(CarDetailEffect.ShowError("Please select both start and end dates"))
            return
        }
        
        // Validate dates
        val dateError = validateDates(startDate, endDate)
        if (dateError != null) {
            setState { copy(dateError = dateError) }
            setEffect(CarDetailEffect.ShowError(dateError))
            return
        }
        
        setState { copy(bookingInProgress = true) }
        
        viewModelScope.launch {
            when (val result = createBookingUseCase(userId, car.id, startDate, endDate)) {
                is Result.Success -> {
                    setState { copy(bookingInProgress = false) }
                    setEffect(CarDetailEffect.BookingSuccess(result.data))
                }
                is Result.Error -> {
                    setState { copy(bookingInProgress = false) }
                    setEffect(CarDetailEffect.ShowError(result.exception.message ?: "Failed to book the car"))
                }
                is Result.Loading -> {
                    setState { copy(bookingInProgress = true) }
                }
            }
        }
    }

    private fun navigateBack() {
        setEffect(CarDetailEffect.NavigateBack)
    }
}