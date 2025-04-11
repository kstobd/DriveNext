package com.example.drivenext.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CarRental
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Класс представляющий элемент нижней навигационной панели
 */
sealed class BottomNavItem(
    val route: String, 
    val title: String, 
    val icon: ImageVector
) {
    // Главный экран
    object Home : BottomNavItem(
        route = "home_screen",
        title = "Главная",
        icon = Icons.Default.Home
    )
    
    // Экран списка бронирований
    object Bookings : BottomNavItem(
        route = "bookings_screen", 
        title = "Бронирования", 
        icon = Icons.Default.List
    )
    
    // Экран настроек
    object Settings : BottomNavItem(
        route = "settings_screen", 
        title = "Настройки", 
        icon = Icons.Default.Settings
    )
    
    // Список всех элементов навигации
    companion object {
        val items = listOf(Home, Bookings, Settings)
    }
}