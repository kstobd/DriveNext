package com.example.drivenext.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.drivenext.domain.model.Booking
import com.example.drivenext.domain.model.Car
import com.example.drivenext.domain.repository.CarRepository
import com.example.drivenext.domain.usecase.GetUserBookingsUseCase
import com.example.drivenext.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для экрана со списком бронирований пользователя.
 * Отвечает за:
 * - Загрузку списка бронирований конкретного пользователя
 * - Загрузку информации об автомобилях для каждого бронирования
 * - Обновление списка бронирований
 * - Навигацию к деталям конкретного бронирования
 */
@HiltViewModel
class BookingListViewModel @Inject constructor(
    private val getUserBookingsUseCase: GetUserBookingsUseCase,
    private val carRepository: CarRepository
) : BaseViewModel<BookingListViewModel.BookingListState, BookingListViewModel.BookingListEvent, BookingListViewModel.BookingListEffect>() {

    /**
     * Класс, объединяющий информацию о бронировании и связанном автомобиле
     * @param booking Информация о бронировании
     * @param car Информация об автомобиле (может быть null во время загрузки)
     */
    data class BookingWithCar(
        val booking: Booking,
        val car: Car? = null
    )
    
    /**
     * Состояние экрана списка бронирований
     * @param bookings Список бронирований с информацией об автомобилях
     * @param isLoading Флаг загрузки данных
     * @param error Текст ошибки, если она возникла
     */
    data class BookingListState(
        val bookings: List<BookingWithCar> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    /**
     * События, которые могут происходить на экране списка бронирований
     * LoadBookings - Запрос на загрузку списка бронирований для конкретного пользователя
     * BookingSelected - Выбор конкретного бронирования из списка
     * RefreshBookings - Запрос на обновление списка бронирований
     */
    sealed class BookingListEvent {
        data class LoadBookings(val userId: Long) : BookingListEvent()
        data class BookingSelected(val bookingId: Long) : BookingListEvent()
        object RefreshBookings : BookingListEvent()
    }

    /**
     * Эффекты UI для экрана списка бронирований
     * NavigateToBookingDetail - Переход к деталям конкретного бронирования
     * ShowError - Отображение сообщения об ошибке
     */
    sealed class BookingListEffect {
        data class NavigateToBookingDetail(val bookingId: Long) : BookingListEffect()
        data class ShowError(val message: String) : BookingListEffect()
    }

    // ID текущего пользователя для обновления списка
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

    /**
     * Загружает список бронирований для указанного пользователя.
     * После успешной загрузки бронирований запускает загрузку информации об автомобилях.
     * @param userId ID пользователя
     */
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
                        setEffect(BookingListEffect.ShowError(result.exception.message ?: "Не удалось загрузить бронирования"))
                    }
                    is Result.Loading -> {
                        setState { copy(isLoading = true) }
                    }
                }
            }
            .catch { e ->
                setState { copy(isLoading = false, error = e.message) }
                setEffect(BookingListEffect.ShowError(e.message ?: "Произошла неожиданная ошибка"))
            }
            .launchIn(viewModelScope)
    }

    /**
     * Загружает информацию об автомобилях для каждого бронирования.
     * Обновляет состояние по мере получения информации о каждом автомобиле.
     * @param bookings Список бронирований
     */
    private fun loadCarDetails(bookings: List<Booking>) {
        for (booking in bookings) {
            viewModelScope.launch {
                when (val result = carRepository.getCarById(booking.carId)) {
                    is Result.Success -> {
                        val car = result.data
                        updateBookingWithCar(booking, car)
                    }
                    else -> {
                        // Игнорируем ошибки загрузки деталей автомобиля, 
                        // так как это не критично для работы списка бронирований
                    }
                }
            }
        }
    }

    /**
     * Обновляет информацию об автомобиле для конкретного бронирования в списке.
     * @param booking Бронирование
     * @param car Информация об автомобиле
     */
    private fun updateBookingWithCar(booking: Booking, car: Car) {
        val currentBookings = state.value.bookings.toMutableList()
        val index = currentBookings.indexOfFirst { it.booking.id == booking.id }
        if (index != -1) {
            currentBookings[index] = BookingWithCar(booking, car)
            setState { copy(bookings = currentBookings) }
        }
    }

    /**
     * Инициирует переход к экрану деталей конкретного бронирования
     * @param bookingId ID бронирования
     */
    private fun navigateToBookingDetail(bookingId: Long) {
        setEffect(BookingListEffect.NavigateToBookingDetail(bookingId))
    }
}