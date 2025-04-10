package com.example.drivenext.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.drivenext.domain.model.Car
import com.example.drivenext.domain.repository.CarRepository
import com.example.drivenext.domain.usecase.CreateBookingUseCase
import com.example.drivenext.utils.Result
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

/**
 * ViewModel для экрана детальной информации об автомобиле.
 * Отвечает за:
 * - Загрузку и отображение подробной информации об автомобиле
 * - Управление процессом бронирования
 * - Валидацию дат бронирования
 * - Расчет стоимости аренды
 */
@HiltViewModel
class CarDetailViewModel @Inject constructor(
    private val carRepository: CarRepository,
    private val createBookingUseCase: CreateBookingUseCase
) : BaseViewModel<CarDetailViewModel.CarDetailState, CarDetailViewModel.CarDetailEvent, CarDetailViewModel.CarDetailEffect>() {

    /**
     * Состояние экрана деталей автомобиля
     * @param car Информация об автомобиле
     * @param startDate Дата начала аренды
     * @param endDate Дата окончания аренды
     * @param totalPrice Общая стоимость аренды
     * @param isLoading Флаг загрузки данных об автомобиле
     * @param bookingInProgress Флаг процесса бронирования
     * @param dateError Ошибка при выборе дат
     */
    data class CarDetailState(
        val car: Car? = null,
        val startDate: Date? = null,
        val endDate: Date? = null,
        val totalPrice: Double = 0.0,
        val isLoading: Boolean = false,
        val bookingInProgress: Boolean = false,
        val dateError: String? = null
    )

    /**
     * События, происходящие на экране деталей автомобиля
     * LoadCar - Запрос на загрузку информации об автомобиле
     * StartDateSelected - Выбрана дата начала аренды
     * EndDateSelected - Выбрана дата окончания аренды
     * BookCar - Запрос на бронирование автомобиля
     * BackPressed - Нажата кнопка "Назад"
     */
    sealed class CarDetailEvent {
        data class LoadCar(val carId: Long) : CarDetailEvent()
        data class StartDateSelected(val date: Date) : CarDetailEvent()
        data class EndDateSelected(val date: Date) : CarDetailEvent()
        data class BookCar(val userId: Long) : CarDetailEvent()
        object BackPressed : CarDetailEvent()
    }

    /**
     * Эффекты UI для экрана деталей автомобиля
     * NavigateBack - Возврат к предыдущему экрану
     * ShowError - Отображение ошибки
     * BookingSuccess - Успешное бронирование
     */
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

    /**
     * Загружает информацию об автомобиле по его ID
     * @param carId ID автомобиля
     */
    private fun loadCar(carId: Long) {
        setState { copy(isLoading = true) }
        
        viewModelScope.launch {
            when (val result = carRepository.getCarById(carId)) {
                is Result.Success -> {
                    setState { copy(car = result.data, isLoading = false) }
                }
                is Result.Error -> {
                    setState { copy(isLoading = false) }
                    setEffect(CarDetailEffect.ShowError(result.exception.message ?: "Не удалось загрузить информацию об автомобиле"))
                    setEffect(CarDetailEffect.NavigateBack)
                }
                is Result.Loading -> {
                    setState { copy(isLoading = true) }
                }
            }
        }
    }

    /**
     * Обновляет дату начала аренды и пересчитывает стоимость
     * @param date Выбранная дата начала аренды
     */
    private fun updateStartDate(date: Date) {
        setState { 
            val startDate = date
            val endDate = this.endDate
            val car = this.car
            
            // Рассчитываем общую стоимость, если выбраны обе даты
            if (endDate != null && car != null) {
                val days = ((endDate.time - startDate.time) / (1000 * 60 * 60 * 24)).toInt() + 1
                val totalPrice = car.pricePerDay * days
                copy(startDate = startDate, totalPrice = totalPrice, dateError = validateDates(startDate, endDate))
            } else {
                copy(startDate = startDate, dateError = null)
            }
        }
    }

    /**
     * Обновляет дату окончания аренды и пересчитывает стоимость
     * @param date Выбранная дата окончания аренды
     */
    private fun updateEndDate(date: Date) {
        setState { 
            val startDate = this.startDate
            val endDate = date
            val car = this.car
            
            if (startDate != null && car != null) {
                val days = ((endDate.time - startDate.time) / (1000 * 60 * 60 * 24)).toInt() + 1
                val totalPrice = car.pricePerDay * days
                copy(endDate = endDate, totalPrice = totalPrice, dateError = validateDates(startDate, endDate))
            } else {
                copy(endDate = endDate, dateError = null)
            }
        }
    }

    /**
     * Проверяет корректность выбранных дат
     * @param startDate Дата начала аренды
     * @param endDate Дата окончания аренды
     * @return Текст ошибки или null, если даты корректны
     */
    private fun validateDates(startDate: Date, endDate: Date): String? {
        val now = Date()
        return when {
            startDate.before(now) -> "Дата начала аренды не может быть в прошлом"
            endDate.before(startDate) -> "Дата окончания аренды не может быть раньше даты начала"
            else -> null
        }
    }

    /**
     * Создает новое бронирование автомобиля
     * @param userId ID пользователя, создающего бронирование
     */
    private fun bookCar(userId: Long) {
        val currentState = state.value
        val car = currentState.car ?: return
        val startDate = currentState.startDate ?: return
        val endDate = currentState.endDate ?: return
        
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
                    setEffect(CarDetailEffect.ShowError(result.exception.message ?: "Не удалось забронировать автомобиль"))
                }
                is Result.Loading -> {
                    setState { copy(bookingInProgress = true) }
                }
            }
        }
    }

    /**
     * Возвращает пользователя к предыдущему экрану
     */
    private fun navigateBack() {
        setEffect(CarDetailEffect.NavigateBack)
    }
}