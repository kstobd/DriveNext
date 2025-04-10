package com.example.drivenext.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.drivenext.domain.model.User
import com.example.drivenext.presentation.util.LocalNetworkConnectivity
import com.example.drivenext.presentation.viewmodel.LoginViewModel

/**
 * Login screen component for user authentication
 */

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: (User) -> Unit,
    onShowError: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val networkConnectivity = LocalNetworkConnectivity.current
    val isConnected by networkConnectivity.observeNetworkStatus().collectAsState(initial = true)

    // Handle UI effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginViewModel.LoginEffect.NavigateToHome -> {
                    onNavigateToHome(effect.user)
                }
                is LoginViewModel.LoginEffect.NavigateToRegister -> {
                    onNavigateToRegister()
                }
                is LoginViewModel.LoginEffect.ShowError -> {
                    onShowError(effect.message)
                }
            }
        }
    }

    if (!isConnected) {
        NoConnectionScreen(
            onRetry = { viewModel.setEvent(LoginViewModel.LoginEvent.RetryConnection) }
        )
        return
    }

    LoginScreenContent(
        email = state.email,
        password = state.password, 
        emailError = state.emailError,
        passwordError = state.passwordError,
        isLoading = state.isLoading,
        onEmailChange = { viewModel.setEvent(LoginViewModel.LoginEvent.EmailChanged(it)) },
        onPasswordChange = { viewModel.setEvent(LoginViewModel.LoginEvent.PasswordChanged(it)) },
        onLoginClick = { viewModel.setEvent(LoginViewModel.LoginEvent.LoginClicked) },
        onRegisterClick = { viewModel.setEvent(LoginViewModel.LoginEvent.RegisterClicked) }
    )
}

@Composable
private fun LoginScreenContent(
    email: String,
    password: String,
    emailError: String? = null,
    passwordError: String? = null,
    isLoading: Boolean = false,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App title/logo
            Text(
                text = "DriveNext",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Car Rental App",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Spacer(modifier = Modifier.height(48.dp))

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                isError = emailError != null,
                supportingText = emailError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = passwordError != null,
                supportingText = passwordError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login button
            Button(
                onClick = onLoginClick,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Login")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Google Sign-In button
            OutlinedButton(
                onClick = { /* Функционал будет добавлен позже */ },
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Войти через Google")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Register text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account?")
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(onClick = onRegisterClick) {
                    Text("Register")
                }
            }
        }
    }
}

@Preview(name = "Экран входа", showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreenContent(
            email = "",
            password = "",
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onRegisterClick = {}
        )
    }
}