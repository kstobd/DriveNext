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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.drivenext.domain.model.User
import com.example.drivenext.presentation.viewmodel.LoginViewModel
import com.example.drivenext.utils.NetworkConnectivity

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
    val isConnected by NetworkConnectivity.connectivityState()

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
        NoConnectionScreen()
        return
    }

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
                value = state.email,
                onValueChange = { viewModel.setEvent(LoginViewModel.LoginEvent.EmailChanged(it)) },
                label = { Text("Email") },
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

            // Password field
            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.setEvent(LoginViewModel.LoginEvent.PasswordChanged(it)) },
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = state.passwordError != null,
                supportingText = state.passwordError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login button
            Button(
                onClick = { viewModel.setEvent(LoginViewModel.LoginEvent.LoginClicked) },
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
                    Text("Login")
                }
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
                TextButton(onClick = { viewModel.setEvent(LoginViewModel.LoginEvent.RegisterClicked) }) {
                    Text("Register")
                }
            }
        }
    }
}