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
 * Base ViewModel that provides common functionality for all ViewModels
 */
abstract class BaseViewModel<State, Event, Effect> : ViewModel() {
    
    private val initialState: State by lazy { createInitialState() }
    
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()
    
    private val _effect = Channel<Effect>()
    val effect = _effect.receiveAsFlow()
    
    /**
     * Create the initial state for this ViewModel
     */
    abstract fun createInitialState(): State
    
    /**
     * Handle events from the UI
     */
    abstract fun handleEvent(event: Event)
    
    /**
     * Set a new event
     */
    fun setEvent(event: Event) {
        handleEvent(event)
    }
    
    /**
     * Update state
     */
    protected fun setState(reducer: State.() -> State) {
        val newState = _state.value.reducer()
        _state.value = newState
    }
    
    /**
     * Set effect
     */
    protected fun setEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}