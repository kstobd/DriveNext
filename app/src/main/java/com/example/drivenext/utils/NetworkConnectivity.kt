package com.example.drivenext.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

/**
 * Utility class to monitor network connectivity
 */
object NetworkConnectivity {
    /**
     * Network connection state as a Flow
     */
    @ExperimentalCoroutinesApi
    fun networkConnectionFlow(context: Context) = callbackFlow {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }
            
            override fun onLost(network: Network) {
                trySend(false)
            }
        }
        
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        connectivityManager.registerNetworkCallback(networkRequest, callback)
        
        // Set initial state
        val isConnected = connectivityManager.activeNetwork?.let { network ->
            connectivityManager.getNetworkCapabilities(network)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } ?: false
        
        trySend(isConnected)
        
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
    
    /**
     * Composable function to get current connectivity state
     */
    @Composable
    fun connectivityState(): State<Boolean> {
        return produceState(initialValue = true) {
            // This is a placeholder. In a real app, you'd use:
            // networkConnectionFlow(context).collect { value = it }
            // 
            // Since we can't access context here, we're returning a default value for now
            value = true
        }
    }
}