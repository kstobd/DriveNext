package com.example.drivenext.presentation.components

import androidx.compose.runtime.staticCompositionLocalOf
import com.example.drivenext.utils.NetworkConnectivity

/**
 * CompositionLocal для обеспечения доступа к объекту NetworkConnectivity
 * в любом Composable-компоненте в дереве композиции
 */
val LocalNetworkConnectivity = staticCompositionLocalOf<NetworkConnectivity> {
    error("LocalNetworkConnectivity не предоставлен. Убедитесь, что он предоставлен в корневом компоненте приложения.")
}