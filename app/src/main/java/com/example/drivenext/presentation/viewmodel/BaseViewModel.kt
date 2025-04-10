package com.example.drivenext.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Базовый класс ViewModel, реализующий паттерн MVI (Model-View-Intent).
 * Предоставляет общий функционал для всех ViewModel в приложении:
 * - Управление состоянием через State
 * - Обработка событий через Event
 * - Отправка эффектов через Effect
 *
 * @param State Тип данных, представляющий состояние экрана
 * @param Event Тип данных, представляющий события пользовательского интерфейса
 * @param Effect Тип данных, представляющий одноразовые эффекты UI (например, навигация или показ Snackbar)
 */
abstract class BaseViewModel<State, Event, Effect> : ViewModel() {
    
    // Инициализация начального состояния при первом обращении
    private val initialState: State by lazy { createInitialState() }
    
    // Поток для хранения и обновления состояния
    private val _state = MutableStateFlow(initialState)
    
    /**
     * Публичный неизменяемый поток состояния для наблюдения из UI
     */
    val state: StateFlow<State> = _state.asStateFlow()
    
    // Канал для отправки эффектов
    private val _effect = Channel<Effect>()
    
    /**
     * Публичный поток эффектов для наблюдения из UI
     */
    val effect = _effect.receiveAsFlow()
    
    /**
     * Создает начальное состояние для ViewModel.
     * Должен быть реализован в каждом наследнике.
     * @return Начальное состояние типа State
     */
    abstract fun createInitialState(): State
    
    /**
     * Обрабатывает события пользовательского интерфейса.
     * Должен быть реализован в каждом наследнике.
     * @param event Событие типа Event для обработки
     */
    abstract fun handleEvent(event: Event)
    
    /**
     * Метод для установки нового события.
     * Вызывается из UI для передачи событий в ViewModel.
     * @param event Событие типа Event для обработки
     */
    fun setEvent(event: Event) {
        handleEvent(event)
    }
    
    /**
     * Метод для обновления состояния.
     * Использует lambda-функцию для безопасного изменения текущего состояния.
     * @param reducer Функция, принимающая текущее состояние и возвращающая новое
     */
    protected fun setState(reducer: State.() -> State) {
        val newState = _state.value.reducer()
        _state.value = newState
    }
    
    /**
     * Метод для отправки эффекта.
     * Использует корутину для асинхронной отправки эффекта через канал.
     * @param effect Эффект типа Effect для отправки
     */
    protected fun setEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}