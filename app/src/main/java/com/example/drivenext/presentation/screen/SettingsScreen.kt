package com.example.drivenext.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.drivenext.data.local.UserPreferencesManager
import com.example.drivenext.presentation.viewmodel.SettingsViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import android.net.Uri
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

/**
 * Экран настроек пользователя
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToProfile: (Long) -> Unit,
    userId: Long, // Добавляем параметр userId
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Загружаем данные пользователя при запуске экрана, используя переданный ID
    LaunchedEffect(key1 = userId) {
        viewModel.handleEvent(SettingsViewModel.SettingsEvent.LoadUserData(userId))
    }
    
    // Обработка эффектов
    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsViewModel.SettingsEffect.NavigateToProfile -> {
                    onNavigateToProfile(effect.userId)
                }
                is SettingsViewModel.SettingsEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                // Отображаем индикатор загрузки
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.user != null) {
                // Профиль пользователя
                ProfileSection(
                    name = "${state.user?.lastName ?: ""} ${state.user?.firstName ?: ""} ${state.user?.middleName ?: ""}".trim(),
                    email = state.user?.email ?: "",
                    userPhotoUri = state.userPhotoUri,
                    onProfileClick = { 
                        viewModel.handleEvent(SettingsViewModel.SettingsEvent.ProfileClicked)
                    }
                )
                
                // Отладочная информация - для поиска проблемы с фото
                // Text(
                    // text = "Debug: Photo URI = ${state.userPhotoUri ?: "NULL"}",
                //     style = MaterialTheme.typography.bodySmall,
                //     color = MaterialTheme.colorScheme.error,
                //     modifier = Modifier.padding(horizontal = 16.dp)
                // )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // Настройки приложения
                SettingsItem(
                    icon = Icons.Default.ColorLens,
                    title = "Тема",
                    onClick = { /* Действие */ }
                )
                
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Уведомления",
                    onClick = { /* Действие */ }
                )
                
                SettingsItem(
                    icon = Icons.Default.DirectionsCar,
                    title = "Подключить свой автомобиль",
                    onClick = { /* Действие */ }
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // Дополнительные настройки
                SettingsItem(
                    icon = Icons.Default.Help,
                    title = "Помощь",
                    onClick = { /* Действие */ }
                )
                
                SettingsItem(
                    icon = Icons.Default.Share,
                    title = "Пригласи друга",
                    onClick = { /* Действие */ }
                )
            } else if (state.error != null) {
                // Отображаем сообщение об ошибке
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ошибка загрузки данных пользователя",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/**
 * Секция профиля пользователя
 */
@Composable
fun ProfileSection(
    name: String,
    email: String,
    userPhotoUri: String? = null,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onProfileClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Аватар пользователя
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (userPhotoUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(Uri.parse(userPhotoUri))
                        .crossfade(true)
                        .build(),
                    contentDescription = "Фото профиля",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Аватар",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Перейти",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

/**
 * Элемент настроек
 */
@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        )
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Перейти",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}