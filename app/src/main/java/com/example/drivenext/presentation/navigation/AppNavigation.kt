package com.example.drivenext.presentation.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.drivenext.domain.model.User
import com.example.drivenext.presentation.screen.CarListScreen
import com.example.drivenext.presentation.screen.LoginScreen
import com.example.drivenext.presentation.screen.MainScreen
import com.example.drivenext.presentation.screen.OnboardingScreen
import com.example.drivenext.presentation.screen.ProfileScreen
import com.example.drivenext.presentation.screen.RegisterScreen
import com.example.drivenext.presentation.screen.RegisterStep2Screen
import com.example.drivenext.presentation.screen.RegisterStep3Screen
import com.example.drivenext.presentation.screen.SettingsScreen
import com.example.drivenext.presentation.screen.WelcomeScreen
import com.example.drivenext.presentation.viewmodel.OnboardingViewModel
import kotlinx.coroutines.launch

/**
 * Главный навигационный компонент приложения.
 * Отвечает за:
 * - Определение всех доступных экранов
 * - Настройку навигационных маршрутов
 * - Передачу параметров между экранами
 * - Обработку навигационных действий
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Получаем OnboardingViewModel для определения начального экрана
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    
    // Определяем начальный экран на основе состояния приложения
    val startDestination = when {
        onboardingViewModel.hasValidToken() -> Screen.Main.createRoute(0) // Сразу к главному экрану если есть токен
        !onboardingViewModel.isOnboardingCompleted() -> Screen.Onboarding.route // К онбордингу если не пройден
        else -> Screen.Welcome.route // К приветственному экрану в остальных случаях
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding Screen
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(
                onBoardingFinished = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Welcome Screen
        composable(route = Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route) {
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
                    // Navigate to main screen and pass user ID
                    navController.navigate(Screen.Main.createRoute(user.id)) {
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
                onNavigateToRegisterStep2 = { userId ->
                    navController.navigate(Screen.RegisterStep2.createRoute(userId))
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

        // Register Step 2 Screen
        composable(
            route = Screen.RegisterStep2.route + "/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            RegisterStep2Screen(
                userId = userId,
                onNavigateToHome = { 
                    navController.navigate(Screen.Main.createRoute(userId)) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToRegisterStep3 = { userId ->
                    navController.navigate(Screen.RegisterStep3.createRoute(userId))
                },
                onShowError = { message ->
                    scope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                },
                onShowSuccess = { message ->
                    scope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                }
            )
        }
        
        // Register Step 3 Screen
        composable(
            route = Screen.RegisterStep3.route + "/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            RegisterStep3Screen(
                userId = userId,
                onNavigateToHome = { 
                    navController.navigate(Screen.Main.createRoute(userId)) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onShowError = { message ->
                    scope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                },
                onShowSuccess = { message ->
                    scope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                }
            )
        }

        // Main Screen с нижней навигацией
        composable(
            route = Screen.Main.route + "/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            MainScreen(userId = userId)
        }
        
        // Car List Screen (теперь будет встроен в MainScreen)
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
        
        // Settings Screen
        // Изменяем определение экрана настроек, чтобы добавить userId параметр
        composable(
            route = Screen.Settings.route + "/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            SettingsScreen(
                userId = userId,
                onNavigateToProfile = { profileUserId ->
                    navController.navigate(Screen.Profile.createRoute(profileUserId))
                }
            )
        }
        
        // Profile Screen
        composable(
            route = Screen.Profile.route + "/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId") ?: 0L
            ProfileScreen(
                userId = userId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

/**
 * Sealed класс, определяющий все экраны приложения и их маршруты.
 * Каждый объект представляет отдельный экран приложения.
 * Для экранов, требующих параметры, определены методы createRoute для формирования полного пути.
 */
sealed class Screen(val route: String) {
    /** Экран онбординга для первого запуска */
    object Onboarding : Screen("onboarding")
    
    /** Приветственный экран */
    object Welcome : Screen("welcome")
    
    /** Экран входа */
    object Login : Screen("login")
    
    /** Экран регистрации (шаг 1) */
    object Register : Screen("register")
    
    /** Экран регистрации (шаг 2) с указанием персональных данных */
    object RegisterStep2 : Screen("register_step2") {
        fun createRoute(userId: Long): String = "$route/$userId"
    }
    
    /** Экран регистрации (шаг 3) с загрузкой документов */
    object RegisterStep3 : Screen("register_step3") {
        fun createRoute(userId: Long): String = "$route/$userId"
    }
    
    /** Основной экран приложения с нижней навигацией */
    object Main : Screen("main") {
        fun createRoute(userId: Long): String = "$route/$userId"
    }
    
    /** Экран со списком доступных автомобилей */
    object CarList : Screen("car_list") {
        fun createRoute(userId: Long): String = "$route/$userId"
    }
    
    /** Экран с подробной информацией об автомобиле */
    object CarDetail : Screen("car_detail") {
        fun createRoute(carId: Long, userId: Long): String = "$route/$carId/$userId"
    }
    
    /** Экран со списком бронирований пользователя */
    object BookingList : Screen("booking_list") {
        fun createRoute(userId: Long): String = "$route/$userId"
    }
    
    /** Экран настроек */
    object Settings : Screen("settings") {
        fun createRoute(userId: Long): String = "$route/$userId"
    }
    
    /** Экран профиля пользователя */
    object Profile : Screen("profile") {
        fun createRoute(userId: Long): String = "$route/$userId"
    }
}