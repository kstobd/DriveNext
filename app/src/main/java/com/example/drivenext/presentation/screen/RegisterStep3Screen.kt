package com.example.drivenext.presentation.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.drivenext.presentation.util.LocalNetworkConnectivity
import com.example.drivenext.presentation.viewmodel.RegisterStep3ViewModel

/**
 * Экран третьего шага регистрации: загрузка документов пользователя
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterStep3Screen(
    userId: Long,
    viewModel: RegisterStep3ViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    onShowError: (String) -> Unit,
    onShowSuccess: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val networkConnectivity = LocalNetworkConnectivity.current
    val isConnected by networkConnectivity.observeNetworkStatus().collectAsState(initial = true)
    val context = LocalContext.current

    // Устанавливаем userId при первом запуске экрана
    LaunchedEffect(userId) {
        viewModel.handleEvent(RegisterStep3ViewModel.RegisterStep3Event.SetUserId(userId))
    }

    // Обрабатываем эффекты UI
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                RegisterStep3ViewModel.RegisterStep3Effect.NavigateBack -> {
                    onNavigateBack()
                }
                RegisterStep3ViewModel.RegisterStep3Effect.NavigateToHome -> {
                    onNavigateToHome()
                }
                is RegisterStep3ViewModel.RegisterStep3Effect.ShowError -> {
                    onShowError(effect.message)
                }
                is RegisterStep3ViewModel.RegisterStep3Effect.ShowSuccess -> {
                    onShowSuccess(effect.message)
                }
            }
        }
    }

    // Проверяем подключение к сети
    if (!isConnected) {
        NoConnectionScreen(
            onRetry = { /* Действие при повторной попытке подключения */ }
        )
        return
    }

    // Лаунчеры для выбора изображений
    val profileImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.handleEvent(RegisterStep3ViewModel.RegisterStep3Event.ProfileImageSelected(it))
        }
    }

    val driverLicenseLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.handleEvent(RegisterStep3ViewModel.RegisterStep3Event.DriverLicenseImageSelected(it))
        }
    }

    val passportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.handleEvent(RegisterStep3ViewModel.RegisterStep3Event.PassportImageSelected(it))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Загрузка документов") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleEvent(RegisterStep3ViewModel.RegisterStep3Event.BackClicked) }) {
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
            // Добавить фотографию профиля (необязательно)
            Text(
                text = "Фото профиля",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { profileImageLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (state.profileImageUri != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(state.profileImageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Фото профиля",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Добавить фото профиля",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            
            Text(
                text = "Необязательно",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Водительское удостоверение
            Text(
                text = "Водительское удостоверение",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Номер водительского удостоверения
            OutlinedTextField(
                value = state.driverLicenseNumber,
                onValueChange = { viewModel.handleEvent(RegisterStep3ViewModel.RegisterStep3Event.LicenseNumberChanged(it)) },
                label = { Text("Номер водительского удостоверения") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                isError = state.licenseNumberError != null,
                supportingText = state.licenseNumberError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Дата выдачи водительского удостоверения
            OutlinedTextField(
                value = state.driverLicenseIssueDate,
                onValueChange = { viewModel.handleEvent(RegisterStep3ViewModel.RegisterStep3Event.LicenseDateChanged(it)) },
                label = { Text("Дата выдачи (ДД/ММ/ГГГГ)") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                trailingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Выбрать дату"
                    )
                },
                isError = state.licenseDateError != null,
                supportingText = state.licenseDateError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Фото водительского удостоверения
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.documentsError != null && state.driverLicenseImageUri == null) 
                        MaterialTheme.colorScheme.errorContainer 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clickable { driverLicenseLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.driverLicenseImageUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(state.driverLicenseImageUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Фото водительского удостоверения",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Camera,
                                contentDescription = "Загрузить фото",
                                tint = if (state.documentsError != null && state.driverLicenseImageUri == null) 
                                    MaterialTheme.colorScheme.error 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Загрузить фото водительского удостоверения",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = if (state.documentsError != null && state.driverLicenseImageUri == null) 
                                    MaterialTheme.colorScheme.error 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Паспорт
            Text(
                text = "Паспорт",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Фото паспорта
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.documentsError != null && state.passportImageUri == null) 
                        MaterialTheme.colorScheme.errorContainer 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clickable { passportLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.passportImageUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(state.passportImageUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Фото паспорта",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Camera,
                                contentDescription = "Загрузить фото",
                                tint = if (state.documentsError != null && state.passportImageUri == null) 
                                    MaterialTheme.colorScheme.error 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Загрузить фото паспорта",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = if (state.documentsError != null && state.passportImageUri == null) 
                                    MaterialTheme.colorScheme.error 
                                else 
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Отображение общей ошибки для загрузки документов
            if (state.documentsError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.documentsError ?: "", // Добавляем оператор элвиса для безопасной работы с nullable
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Кнопка "Далее"
            Button(
                onClick = { viewModel.handleEvent(RegisterStep3ViewModel.RegisterStep3Event.SubmitClicked) },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Далее")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка "Назад"
            OutlinedButton(
                onClick = { viewModel.handleEvent(RegisterStep3ViewModel.RegisterStep3Event.BackClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Назад")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}