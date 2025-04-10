package com.example.drivenext.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.drivenext.presentation.util.LocalNetworkConnectivity
import com.example.drivenext.presentation.viewmodel.RegisterStep2ViewModel
import com.example.drivenext.presentation.viewmodel.RegisterStep2ViewModel.Gender
import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 * Экран ввода дополнительной информации пользователя (шаг 2 регистрации)
 */
@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterStep2Screen(
    userId: Long,
    viewModel: RegisterStep2ViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToRegisterStep3: (Long) -> Unit,  // Добавляем навигацию на третий шаг
    onShowError: (String) -> Unit,
    onShowSuccess: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val networkConnectivity = LocalNetworkConnectivity.current
    val isConnected by networkConnectivity.observeNetworkStatus().collectAsState(initial = true)
    
    // Добавляем состояние для отображения диалога с датой
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd.MM.yyyy") }

    // Установка ID пользователя при первой загрузке экрана
    LaunchedEffect(userId) {
        viewModel.handleEvent(RegisterStep2ViewModel.RegisterStep2Event.SetUserId(userId))
    }

    // Обработка эффектов
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                RegisterStep2ViewModel.RegisterStep2Effect.NavigateToHome -> {
                    onNavigateToHome()
                }
                RegisterStep2ViewModel.RegisterStep2Effect.NavigateBack -> {
                    onNavigateBack()
                }
                is RegisterStep2ViewModel.RegisterStep2Effect.NavigateToRegisterStep3 -> {
                    onNavigateToRegisterStep3(effect.userId)
                }
                is RegisterStep2ViewModel.RegisterStep2Effect.ShowError -> {
                    onShowError(effect.message)
                }
                is RegisterStep2ViewModel.RegisterStep2Effect.ShowSuccess -> {
                    onShowSuccess(effect.message)
                }
            }
        }
    }

    if (!isConnected) {
        NoConnectionScreen(
            onRetry = { /* Повторить подключение */ }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Дополнительная информация") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleEvent(RegisterStep2ViewModel.RegisterStep2Event.BackClicked) }) {
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
            Text(
                text = "Введите личные данные",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Фамилия (обязательное поле)
            OutlinedTextField(
                value = state.lastName,
                onValueChange = { viewModel.handleEvent(RegisterStep2ViewModel.RegisterStep2Event.LastNameChanged(it)) },
                label = { Text("Фамилия*") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                isError = state.lastNameError != null,
                supportingText = state.lastNameError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Имя (обязательное поле)
            OutlinedTextField(
                value = state.firstName,
                onValueChange = { viewModel.handleEvent(RegisterStep2ViewModel.RegisterStep2Event.FirstNameChanged(it)) },
                label = { Text("Имя*") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                isError = state.firstNameError != null,
                supportingText = state.firstNameError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Отчество (необязательное поле)
            OutlinedTextField(
                value = state.middleName,
                onValueChange = { viewModel.handleEvent(RegisterStep2ViewModel.RegisterStep2Event.MiddleNameChanged(it)) },
                label = { Text("Отчество (необязательно)") },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Дата рождения (обязательное поле)
            OutlinedTextField(
                value = state.birthDate,
                onValueChange = { viewModel.handleEvent(RegisterStep2ViewModel.RegisterStep2Event.BirthDateChanged(it)) },
                label = { Text("Дата рождения* (ДД.ММ.ГГГГ)") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                isError = state.birthDateError != null,
                supportingText = state.birthDateError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Выбрать дату")
                    }
                }
            )

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    onDateChange = { date ->
                        viewModel.handleEvent(RegisterStep2ViewModel.RegisterStep2Event.BirthDateChanged(date.format(dateFormatter)))
                        showDatePicker = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Пол (обязательное поле)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Пол*",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (state.genderError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = state.gender == Gender.MALE,
                        onClick = { viewModel.handleEvent(RegisterStep2ViewModel.RegisterStep2Event.GenderChanged(Gender.MALE)) }
                    )
                    Text(
                        text = "Мужской",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = state.gender == Gender.FEMALE,
                        onClick = { viewModel.handleEvent(RegisterStep2ViewModel.RegisterStep2Event.GenderChanged(Gender.FEMALE)) }
                    )
                    Text(
                        text = "Женский",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                
                // Показываем ошибку, если есть
                state.genderError?.let { errorText ->
                    Text(
                        text = errorText,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Кнопки действий
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Кнопка "Далее"
                Button(
                    onClick = { viewModel.handleEvent(RegisterStep2ViewModel.RegisterStep2Event.NextClicked) },
                    enabled = !state.isLoading && 
                              state.firstName.isNotBlank() && 
                              state.lastName.isNotBlank() && 
                              state.birthDate.isNotBlank() && 
                              state.gender != null,
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

                Spacer(modifier = Modifier.height(12.dp))

                // Кнопка "Назад"
                OutlinedButton(
                    onClick = { viewModel.handleEvent(RegisterStep2ViewModel.RegisterStep2Event.BackClicked) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Назад")
                }
            }
        }
    }
}

/**
 * Компонент диалога для выбора даты
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateChange: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState()

    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val localDate = java.time.Instant
                            .ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        onDateChange(localDate)
                    }
                }
            ) {
                Text("Подтвердить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Отмена")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            title = { Text("Выберите дату рождения") },
            headline = { Text("Пожалуйста, укажите вашу дату рождения") }
        )
    }
}