package com.example.drivenext.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.drivenext.presentation.components.NoConnectionScreen
import com.example.drivenext.presentation.util.LocalNetworkConnectivity
import com.example.drivenext.presentation.viewmodel.LoginViewModel
import com.example.drivenext.utils.NetworkConnectivity

/**
 * Login screen composable
 */
@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Получаем состояние сети через LocalNetworkConnectivity
    val networkConnectivity = LocalNetworkConnectivity.current
    val isConnected by networkConnectivity.observeNetworkStatus().collectAsState(initial = true)

    LaunchedEffect(key1 = true) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginViewModel.LoginEffect.NavigateToHome -> {
                    onNavigateToHome()
                }
                is LoginViewModel.LoginEffect.NavigateToRegister -> {
                    onNavigateToRegister()
                }
                is LoginViewModel.LoginEffect.ShowError -> {
                    // In a real app, show a snackbar or toast with the error message
                }
            }
        }
    }

    // Проверяем подключение к интернету
    if (!isConnected) {
        NoConnectionScreen(
            onRetry = { viewModel.setEvent(LoginViewModel.LoginEvent.RetryConnection) }
        )
        return
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "DriveNext",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in to your account",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.setEvent(LoginViewModel.LoginEvent.EmailChanged(it)) },
                label = { Text("Email") },
                isError = state.emailError != null,
                supportingText = {
                    state.emailError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.setEvent(LoginViewModel.LoginEvent.PasswordChanged(it)) },
                label = { Text("Password") },
                isError = state.passwordError != null,
                supportingText = {
                    state.passwordError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.setEvent(LoginViewModel.LoginEvent.LoginClicked) },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign In")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { viewModel.setEvent(LoginViewModel.LoginEvent.RegisterClicked) }
            ) {
                Text("Don't have an account? Sign Up")
            }
        }

        if (state.isLoading) {
            CircularProgressIndicator()
        }
    }
}