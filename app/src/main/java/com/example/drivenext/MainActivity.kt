package com.example.drivenext

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.drivenext.presentation.navigation.AppNavigation
import com.example.drivenext.presentation.screen.NoConnectionScreen
import com.example.drivenext.presentation.util.LocalNetworkConnectivity
import com.example.drivenext.ui.theme.DriveNextTheme
import com.example.drivenext.utils.NetworkConnectivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main entry point for the application
 * Sets up the app theme and navigation
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var networkConnectivity: NetworkConnectivity
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DriveNextTheme {
                // Предоставляем NetworkConnectivity через CompositionLocalProvider
                // для доступа к нему из любого места в приложении
                CompositionLocalProvider(
                    LocalNetworkConnectivity provides networkConnectivity
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        // Получаем текущее состояние подключения
                        val isConnected by networkConnectivity.observeNetworkStatus().collectAsState(initial = true)
                        
                        // Если нет подключения, показываем экран отсутствия подключения на уровне всего приложения
                        if (!isConnected) {
                            NoConnectionScreen(
                                onRetry = { networkConnectivity.checkNetworkConnection() }
                            )
                        } else {
                            // Запускаем AppNavigation только если есть подключение к интернету
                            AppNavigation()
                        }
                    }
                }
            }
        }
    }
}