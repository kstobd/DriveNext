package com.example.drivenext.presentation.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.drivenext.domain.model.User
import com.example.drivenext.presentation.screen.CarListScreen
import com.example.drivenext.presentation.screen.LoginScreen
import com.example.drivenext.presentation.screen.RegisterScreen
import com.example.drivenext.presentation.screen.WelcomeScreen
import kotlinx.coroutines.launch

/**
 * Main navigation component for the application
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        // Welcome Screen
        composable(route = Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        // Login Screen
        composable(route = Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToHome = { user ->
                    // Navigate to home screen and pass user ID
                    navController.navigate(Screen.CarList.createRoute(user.id)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onShowError = { message ->
                    scope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                }
            )
        }

        // Register Screen
        composable(route = Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onShowError = { message ->
                    scope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                },
                onShowSuccess = { message ->
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = message,
                            actionLabel = "Login"
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Register.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        // Car List Screen
        composable(
            route = Screen.CarList.route + "/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            
            // Используем hiltViewModel() вместо viewModel()
            CarListScreen(
                // viewModel передается автоматически по-умолчанию через hiltViewModel()
                onNavigateToCarDetail = { carId ->
                    navController.navigate(Screen.CarDetail.createRoute(carId, userId))
                },
                onShowError = { message ->
                    scope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                }
            )
        }

        // Car Detail Screen
        composable(
            route = Screen.CarDetail.route + "/{carId}/{userId}",
            arguments = listOf(
                navArgument("carId") { type = NavType.LongType },
                navArgument("userId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getLong("carId") ?: 0L
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            // We'll implement this screen later
            // CarDetailScreen(carId = carId, userId = userId)
        }

        // Booking List Screen
        composable(
            route = Screen.BookingList.route + "/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            // We'll implement this screen later
            // BookingListScreen(userId = userId)
        }
    }
}

/**
 * Sealed class representing navigation destinations
 */
sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object CarList : Screen("car_list") {
        fun createRoute(userId: Long) = "car_list/$userId"
    }
    object CarDetail : Screen("car_detail") {
        fun createRoute(carId: Long, userId: Long) = "car_detail/$carId/$userId"
    }
    object BookingList : Screen("booking_list") {
        fun createRoute(userId: Long) = "booking_list/$userId"
    }
}