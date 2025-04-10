package com.example.drivenext.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.drivenext.presentation.util.LocalNetworkConnectivity
import com.example.drivenext.presentation.viewmodel.RegisterViewModel

/**
 * Экран создания нового аккаунта пользователя
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToRegisterStep2: (Long) -> Unit,  // Добавляем callback для перехода на шаг 2
    onShowError: (String) -> Unit,
    onShowSuccess: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val networkConnectivity = LocalNetworkConnectivity.current
    val isConnected by networkConnectivity.observeNetworkStatus().collectAsState(initial = true)

    // Состояния для отображения/скрытия пароля
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    // Состояние для чекбокса соглашения с условиями
    var termsAgreed by remember { mutableStateOf(false) }
    var termsError by remember { mutableStateOf<String?>(null) }

    // Handle UI effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is RegisterViewModel.RegisterEffect.NavigateToLogin -> {
                    onNavigateToLogin()
                }
                is RegisterViewModel.RegisterEffect.ShowError -> {
                    onShowError(effect.message)
                }
                is RegisterViewModel.RegisterEffect.ShowSuccess -> {
                    onShowSuccess(effect.message)
                }
                is RegisterViewModel.RegisterEffect.NavigateToRegisterStep2WithId -> {
                    // Новая обработка для перехода на второй шаг регистрации
                    onNavigateToRegisterStep2(effect.userId)
                }
                is RegisterViewModel.RegisterEffect.NavigateToRegisterStep2 -> {
                    // Этот эффект не используется, но должен быть обработан для полноты when-выражения
                }
            }
        }
    }

    if (!isConnected) {
        NoConnectionScreen(
            onRetry = { viewModel.setEvent(RegisterViewModel.RegisterEvent.RetryConnection) }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Регистрация") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.setEvent(RegisterViewModel.RegisterEvent.LoginClicked) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Name field
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.setEvent(RegisterViewModel.RegisterEvent.NameChanged(it)) },
                label = { Text("Имя") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                isError = state.nameError != null,
                supportingText = state.nameError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email field
            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.setEvent(RegisterViewModel.RegisterEvent.EmailChanged(it)) },
                label = { Text("Электронная почта") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                isError = state.emailError != null,
                supportingText = state.emailError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field with toggle visibility
            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.setEvent(RegisterViewModel.RegisterEvent.PasswordChanged(it)) },
                label = { Text("Придумайте пароль") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = state.passwordError != null,
                supportingText = state.passwordError?.let { { Text(it) } },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Скрыть пароль" else "Показать пароль"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm password field with toggle visibility
            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = { viewModel.setEvent(RegisterViewModel.RegisterEvent.ConfirmPasswordChanged(it)) },
                label = { Text("Повторите пароль") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = state.confirmPasswordError != null,
                supportingText = state.confirmPasswordError?.let { { Text(it) } },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Скрыть пароль" else "Показать пароль"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Terms and Conditions Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = termsAgreed,
                    onCheckedChange = { 
                        termsAgreed = it
                        termsError = null
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Я согласен с условиями обслуживания и политикой конфиденциальности",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Показываем ошибку для чекбокса, если есть
            if (termsError != null) {
                Text(
                    text = termsError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Next button
            Button(
                onClick = { 
                    // Проверка согласия с условиями
                    if (!termsAgreed) {
                        termsError = "Необходимо согласиться с условиями обслуживания и политикой конфиденциальности."
                        return@Button
                    }
                    viewModel.setEvent(RegisterViewModel.RegisterEvent.RegisterClicked) 
                },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Далее")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Login text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Уже есть аккаунт?")
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(onClick = { viewModel.setEvent(RegisterViewModel.RegisterEvent.LoginClicked) }) {
                    Text("Войти")
                }
            }
        }
    }
}