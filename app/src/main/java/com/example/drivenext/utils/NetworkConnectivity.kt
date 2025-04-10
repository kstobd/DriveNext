package com.example.drivenext.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "NetworkConnectivity"

/**
 * Класс для отслеживания состояния подключения к интернету
 */
@Singleton
class NetworkConnectivity @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    // StateFlow для хранения текущего состояния подключения
    private val _connectionStatus = MutableStateFlow(checkInitialConnection())
    
    // Добавляем флаг для отслеживания ручных проверок подключения
    private var manualCheckInProgress = false
    
    /**
     * Проверяет начальное состояние подключения
     */
    private fun checkInitialConnection(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /**
     * Проверяет текущее состояние подключения к интернету
     */
    fun isConnected(): Boolean {
        return _connectionStatus.value
    }

    /**
     * Метод для повторной проверки подключения к интернету
     * Вызывается при нажатии на кнопку "Повторить попытку"
     * 
     * @return текущее состояние подключения
     */
    fun checkNetworkConnection(): Boolean {
        // Устанавливаем флаг, что выполняется ручная проверка
        manualCheckInProgress = true
        val currentState = isNetworkActuallyConnected()
        _connectionStatus.value = currentState
        manualCheckInProgress = false
        return currentState
    }

    /**
     * Проверяет фактическое состояние подключения к сети
     */
    private fun isNetworkActuallyConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /**
     * Flow для отслеживания изменений статуса подключения к интернету
     * 
     * @return Flow<Boolean>, где true - подключение есть, false - подключения нет
     */
    fun observeNetworkStatus(): Flow<Boolean> = callbackFlow {
        // Отправляем начальное состояние сети
        val initialState = isNetworkActuallyConnected()
        trySend(initialState)
        _connectionStatus.value = initialState
        
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            // При появлении сети
            override fun onAvailable(network: Network) {
                Log.d(TAG, "Сеть стала доступна")
                val hasInternet = isNetworkActuallyConnected()
                if (hasInternet) {
                    launch {
                        // Делаем небольшую задержку перед проверкой для стабильности
                        delay(1000)
                        val isStillConnected = isNetworkActuallyConnected()
                        if (isStillConnected) {
                            _connectionStatus.value = true
                            trySend(true)
                        }
                    }
                }
            }

            // При потере сети
            override fun onLost(network: Network) {
                Log.d(TAG, "Сеть потеряна")
                _connectionStatus.value = false
                trySend(false)
            }
            
            // При изменении возможностей сети
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                                 networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                
                Log.d(TAG, "Изменение возможностей сети. Internet доступен: $hasInternet")
                
                if (!hasInternet) {
                    _connectionStatus.value = false
                    trySend(false)
                } else {
                    // При восстановлении подключения - небольшая задержка
                    launch {
                        delay(1000)
                        val isStillConnected = isNetworkActuallyConnected()
                        if (isStillConnected) {
                            _connectionStatus.value = true
                            trySend(true)
                        }
                    }
                }
            }
            
            // Когда сеть недоступна
            override fun onUnavailable() {
                Log.d(TAG, "Сеть недоступна")
                _connectionStatus.value = false
                trySend(false)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        // Регистрируем слушатель изменений сети
        connectivityManager.registerNetworkCallback(request, networkCallback)
        
        // Запускаем периодическую проверку статуса сети для надежности
        launch {
            while (true) {
                delay(5000) // Проверяем каждые 5 секунд
                val currentState = isNetworkActuallyConnected()
                
                Log.d(TAG, "Периодическая проверка сети: $currentState")
                
                if (_connectionStatus.value != currentState) {
                    _connectionStatus.value = currentState
                    trySend(currentState)
                }
            }
        }
        
        // Удаляем слушатель при закрытии Flow
        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }.distinctUntilChanged() // Отправляем события только при изменении состояния
}