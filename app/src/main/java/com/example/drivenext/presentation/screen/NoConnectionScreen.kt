package com.example.drivenext.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.drivenext.R

/**
 * Экран, отображаемый при отсутствии подключения к интернету
 * 
 * @param onRetry Колбэк для повторной попытки подключения
 */
@Composable
fun NoConnectionScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Иконка отсутствия подключения
        Image(
            painter = painterResource(id = R.drawable.ic_no_connection),
            contentDescription = stringResource(id = R.string.no_internet_connection),
            modifier = Modifier.size(120.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Текст сообщения об ошибке
        Text(
            text = stringResource(id = R.string.no_internet_connection),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Подсказка для пользователя
        Text(
            text = stringResource(id = R.string.check_connection),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Кнопка "Повторить попытку"
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Text(text = stringResource(id = R.string.retry))
        }
    }
}