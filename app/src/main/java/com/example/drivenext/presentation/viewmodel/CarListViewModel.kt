package com.example.drivenext.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.drivenext.domain.model.Car
import com.example.drivenext.domain.usecase.GetAvailableCarsUseCase
import com.example.drivenext.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * ViewModel для экрана списка автомобилей.
 * Отвечает за:
 * - Загрузку и отображение списка доступных автомобилей
 * - Обработку событий пользовательского интерфейса
 * - Обработку ошибок загрузки данных
 * - Навигацию к деталям выбранного автомобиля
 */
@HiltViewModel
class CarListViewModel @Inject constructor(
    private val getAvailableCarsUseCase: GetAvailableCarsUseCase
) : BaseViewModel<CarListViewModel.CarListState, CarListViewModel.CarListEvent, CarListViewModel.CarListEffect>() {

    /**
     * Состояние экрана списка автомобилей
     * @param cars Список доступных автомобилей
     * @param isLoading Флаг загрузки данных
     * @param error Текст ошибки, если она возникла
     */
    data class CarListState(
        val cars: List<Car> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    /**
     * События, которые могут происходить на экране списка автомобилей
     * LoadCars - Запрос на загрузку списка автомобилей
     * CarSelected - Пользователь выбрал автомобиль из списка
     * RefreshCars - Запрос на обновление списка автомобилей (например, после потери соединения)
     */
    sealed class CarListEvent {
        object LoadCars : CarListEvent()
        data class CarSelected(val car: Car) : CarListEvent()
        object RefreshCars : CarListEvent()
    }

    /**
     * Эффекты, которые могут быть вызваны действиями на экране
     * NavigateToCarDetail - Переход к экрану деталей автомобиля
     * ShowError - Отображение сообщения об ошибке
     */
    sealed class CarListEffect {
        data class NavigateToCarDetail(val carId: Long) : CarListEffect()
        data class ShowError(val message: String) : CarListEffect()
    }

    override fun createInitialState(): CarListState = CarListState(isLoading = true)

    init {
        try {
            loadCars()
        } catch (e: Exception) {
            setState { copy(isLoading = false, error = e.message ?: "Неизвестная ошибка при загрузке автомобилей") }
        }
    }

    override fun handleEvent(event: CarListEvent) {
        when (event) {
            is CarListEvent.LoadCars -> loadCars()
            is CarListEvent.CarSelected -> navigateToCarDetail(event.car)
            is CarListEvent.RefreshCars -> loadCars()
        }
    }

    /**
     * Метод для загрузки списка автомобилей.
     * Вызывается при первом открытии экрана и при обновлении списка.
     * Использует useCase для получения данных из репозитория.
     */
    fun loadCars() {
        setState { copy(isLoading = true, error = null) }

        try {
            getAvailableCarsUseCase()
                .onEach { result ->
                    when (result) {
                        is Result.Success -> {
                            setState { copy(cars = result.data, isLoading = false, error = null) }
                        }
                        is Result.Error -> {
                            setState { copy(isLoading = false, error = result.exception.message) }
                            setEffect(CarListEffect.ShowError(result.exception.message ?: "Не удалось загрузить автомобили"))
                        }
                        is Result.Loading -> {
                            setState { copy(isLoading = true) }
                        }
                    }
                }
                .catch { e ->
                    setState { copy(isLoading = false, error = e.message) }
                    setEffect(CarListEffect.ShowError(e.message ?: "Произошла неожиданная ошибка"))
                }
                .launchIn(viewModelScope)
        } catch (e: Exception) {
            setState { copy(isLoading = false, error = e.message) }
            setEffect(CarListEffect.ShowError(e.message ?: "Неизвестная ошибка"))
        }
    }

    /**
     * Метод для перехода к экрану деталей выбранного автомобиля
     * @param car Выбранный автомобиль
     */
    private fun navigateToCarDetail(car: Car) {
        setEffect(CarListEffect.NavigateToCarDetail(car.id))
    }
}