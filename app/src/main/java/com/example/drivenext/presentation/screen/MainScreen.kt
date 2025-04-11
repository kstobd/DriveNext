package com.example.drivenext.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.drivenext.presentation.navigation.BottomNavItem
import com.example.drivenext.presentation.navigation.Screen
import kotlinx.coroutines.launch

/**
 * Основной экран приложения с нижней панелью навигации
 */
@Composable
fun MainScreen(userId: Long) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                
                BottomNavItem.items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            // Для экрана настроек добавляем userId в маршрут
                            if (item.route == BottomNavItem.Settings.route) {
                                navController.navigate(Screen.Settings.createRoute(userId)) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            } else {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                        label = { Text(text = item.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                // Заменяем HomeScreen на CarListScreen
                CarListScreen(
                    onNavigateToCarDetail = { carId ->
                        // Навигация к деталям машины
                        navController.navigate(Screen.CarDetail.createRoute(carId, userId))
                    },
                    onShowError = { message ->
                        // Показываем сообщение об ошибке
                        scope.launch {
                            snackbarHostState.showSnackbar(message)
                        }
                    }
                )
            }
            
            composable(BottomNavItem.Bookings.route) {
                BookingsPlaceholder(userId = userId)
            }
            
            // Экран настроек
            composable(
                route = Screen.Settings.route + "/{userId}",
                arguments = listOf(
                    navArgument("userId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                // Получаем userId из аргументов маршрута
                val settingsUserId = backStackEntry.arguments?.getLong("userId") ?: userId
                SettingsScreen(
                    userId = settingsUserId,
                    onNavigateToProfile = { profileUserId ->
                        // Навигация к экрану профиля
                        navController.navigate(Screen.Profile.createRoute(profileUserId))
                    }
                )
            }
            
            // Добавляем маршрут к экрану профиля
            composable(
                route = Screen.Profile.route + "/{userId}",
                arguments = listOf(
                    navArgument("userId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val profileUserId = backStackEntry.arguments?.getLong("userId") ?: userId
                ProfileScreen(
                    userId = profileUserId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

/**
 * Временный заполнитель для экрана бронирований
 */
@Composable
fun BookingsPlaceholder(userId: Long) {
    Box(modifier = Modifier.padding(16.dp)) {
        Text("Экран бронирований будет реализован в будущем")
    }
}