package com.example.drivenext.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Класс для отслеживания состояния подключения к интернету
 */
@Singleton
class NetworkConnectivity @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    // Для отправки актуального состояния подключения
    private val connectionStateFlow = MutableStateFlow(isConnected())

    /**
     * Проверяет текущее состояние подключения к интернету
     */
    fun isConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        // Проверяем наличие интернет-соединения
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /**
     * Метод для повторной проверки подключения к интернету
     * Вызывается при нажатии на кнопку "Повторить попытку"
     * 
     * @return текущее состояние подключения
     */
    fun checkNetworkConnection(): Boolean {
        val currentConnectionState = isConnected()
        connectionStateFlow.value = currentConnectionState
        return currentConnectionState
    }

    /**
     * Flow для отслеживания изменений статуса подключения к интернету
     * 
     * @return Flow<Boolean>, где true - подключение есть, false - подключения нет
     */
    fun observeNetworkStatus(): Flow<Boolean> = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Сеть доступна, отправляем true
                trySend(true)
            }

            override fun onLost(network: Network) {
                // Сеть недоступна, отправляем false
                trySend(false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                // Проверяем возможности сети
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                                 networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                trySend(hasInternet)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        // Регистрируем слушатель изменений сети
        connectivityManager.registerNetworkCallback(request, networkCallback)
        
        // Отправляем начальное состояние сети
        trySend(isConnected())

        // Удаляем слушатель при закрытии Flow
        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }.distinctUntilChanged() // Отправляем события только при изменении состояния
}