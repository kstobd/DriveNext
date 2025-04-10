package com.example.drivenext.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.drivenext.domain.model.Booking
import com.example.drivenext.domain.model.Car
import com.example.drivenext.domain.repository.CarRepository
import com.example.drivenext.domain.usecase.GetUserBookingsUseCase
import com.example.drivenext.utils.Result
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for displaying a user's bookings
 */
class BookingListViewModel @Inject constructor(
    private val getUserBookingsUseCase: GetUserBookingsUseCase,
    private val carRepository: CarRepository
) : BaseViewModel<BookingListViewModel.BookingListState, BookingListViewModel.BookingListEvent, BookingListViewModel.BookingListEffect>() {

    data class BookingWithCar(
        val booking: Booking,
        val car: Car? = null
    )
    
    data class BookingListState(
        val bookings: List<BookingWithCar> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    sealed class BookingListEvent {
        data class LoadBookings(val userId: Long) : BookingListEvent()
        data class BookingSelected(val bookingId: Long) : BookingListEvent()
        object RefreshBookings : BookingListEvent() // Добавлено новое событие для повторной проверки
    }

    sealed class BookingListEffect {
        data class NavigateToBookingDetail(val bookingId: Long) : BookingListEffect()
        data class ShowError(val message: String) : BookingListEffect()
    }

    private var currentUserId: Long = 0

    override fun createInitialState(): BookingListState = BookingListState(isLoading = true)

    override fun handleEvent(event: BookingListEvent) {
        when (event) {
            is BookingListEvent.LoadBookings -> {
                currentUserId = event.userId
                loadBookings(event.userId)
            }
            is BookingListEvent.BookingSelected -> {
                navigateToBookingDetail(event.bookingId)
            }
            is BookingListEvent.RefreshBookings -> {
                loadBookings(currentUserId)
            }
        }
    }

    private fun loadBookings(userId: Long) {
        setState { copy(isLoading = true, error = null) }

        getUserBookingsUseCase(userId)
            .onEach { result ->
                when (result) {
                    is Result.Success -> {
                        val bookingsWithCar = result.data.map { BookingWithCar(it) }
                        setState { copy(bookings = bookingsWithCar, isLoading = false, error = null) }
                        loadCarDetails(result.data)
                    }
                    is Result.Error -> {
                        setState { copy(isLoading = false, error = result.exception.message) }
                        setEffect(BookingListEffect.ShowError(result.exception.message ?: "Failed to load bookings"))
                    }
                    is Result.Loading -> {
                        setState { copy(isLoading = true) }
                    }
                }
            }
            .catch { e ->
                setState { copy(isLoading = false, error = e.message) }
                setEffect(BookingListEffect.ShowError(e.message ?: "An unexpected error occurred"))
            }
            .launchIn(viewModelScope)
    }

    private fun loadCarDetails(bookings: List<Booking>) {
        for (booking in bookings) {
            viewModelScope.launch {
                when (val result = carRepository.getCarById(booking.carId)) {
                    is Result.Success -> {
                        val car = result.data
                        updateBookingWithCar(booking, car)
                    }
                    else -> {
                        // Do nothing or handle error
                    }
                }
            }
        }
    }

    private fun updateBookingWithCar(booking: Booking, car: Car) {
        val currentBookings = state.value.bookings.toMutableList()
        val index = currentBookings.indexOfFirst { it.booking.id == booking.id }
        if (index != -1) {
            currentBookings[index] = BookingWithCar(booking, car)
            setState { copy(bookings = currentBookings) }
        }
    }

    private fun navigateToBookingDetail(bookingId: Long) {
        setEffect(BookingListEffect.NavigateToBookingDetail(bookingId))
    }
}