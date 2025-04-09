package com.example.drivenext.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.drivenext.presentation.viewmodel.RegisterViewModel
import com.example.drivenext.utils.NetworkConnectivity

/**
 * Screen for user registration
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onShowError: (String) -> Unit,
    onShowSuccess: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val isConnected by NetworkConnectivity.connectivityState()

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
            }
        }
    }

    if (!isConnected) {
        NoConnectionScreen()
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.setEvent(RegisterViewModel.RegisterEvent.LoginClicked) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back to Login")
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
                label = { Text("Full Name") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
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

            // Phone number field
            OutlinedTextField(
                value = state.phoneNumber,
                onValueChange = { viewModel.setEvent(RegisterViewModel.RegisterEvent.PhoneChanged(it)) },
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                isError = state.phoneError != null,
                supportingText = state.phoneError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            OutlinedTextField(
                value = state.password,
                onValueChange = { viewModel.setEvent(RegisterViewModel.RegisterEvent.PasswordChanged(it)) },
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = state.passwordError != null,
                supportingText = state.passwordError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm password field
            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = { viewModel.setEvent(RegisterViewModel.RegisterEvent.ConfirmPasswordChanged(it)) },
                label = { Text("Confirm Password") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = state.confirmPasswordError != null,
                supportingText = state.confirmPasswordError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Register button
            Button(
                onClick = { viewModel.setEvent(RegisterViewModel.RegisterEvent.RegisterClicked) },
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
                    Text("Register")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Login text
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account?")
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(onClick = { viewModel.setEvent(RegisterViewModel.RegisterEvent.LoginClicked) }) {
                    Text("Login")
                }
            }
        }
    }
}