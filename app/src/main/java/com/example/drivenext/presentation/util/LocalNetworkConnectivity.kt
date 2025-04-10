package com.example.drivenext.presentation.util

import androidx.compose.runtime.staticCompositionLocalOf
import com.example.drivenext.utils.NetworkConnectivity

/**
 * CompositionLocal для доступа к NetworkConnectivity из любого Compose-компонента
 */
val LocalNetworkConnectivity = staticCompositionLocalOf<NetworkConnectivity> {
    error("LocalNetworkConnectivity не предоставлен. Необходимо обернуть компоненты в CompositionLocalProvider.")
}