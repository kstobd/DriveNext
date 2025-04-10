package com.example.drivenext.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.drivenext.presentation.screen.LoginScreen
import com.example.drivenext.presentation.screen.RegisterScreen
import com.example.drivenext.presentation.screen.RegisterStep2Screen
import com.example.drivenext.presentation.screen.WelcomeScreen
import com.google.accompanist.pager.ExperimentalPagerApi

/**
 * Основная навигация приложения
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Welcome.route,
    showSnackbar: (String) -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        // Экран приветствия
        composable(route = Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // Экран входа
        composable(route = Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    // После успешного входа перейти на домашний экран
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onShowError = { message ->
                    showSnackbar(message)
                }
            )
        }

        // Экран первого шага регистрации
        composable(route = Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToRegisterStep2 = { userId ->
                    navController.navigate(Screen.RegisterStep2.createRoute(userId))
                },
                onShowError = { message ->
                    showSnackbar(message)
                },
                onShowSuccess = { message ->
                    showSnackbar(message)
                }
            )
        }

        // Экран второго шага регистрации
        composable(
            route = Screen.RegisterStep2.route + "/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.LongType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            RegisterStep2Screen(
                userId = userId,
                onNavigateToHome = {
                    // После успешной регистрации перейти на домашний экран
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onShowError = { message ->
                    showSnackbar(message)
                },
                onShowSuccess = { message ->
                    showSnackbar(message)
                }
            )
        }

        // Главный экран приложения (после регистрации/входа)
        composable(route = Screen.Home.route) {
            // HomeScreen() - будет реализован позже
            // Временно можно использовать простое содержимое
        }
    }
}

/**
 * Определение всех экранов приложения
 */
sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object Register : Screen("register")
    object RegisterStep2 : Screen("register_step2") {
        fun createRoute(userId: Long): String = "$route/$userId"
    }
    object Home : Screen("home")
}