package com.example.drivenext.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.drivenext.presentation.viewmodel.CarDetailViewModel
import com.example.drivenext.presentation.viewmodel.CarDetailViewModel.CarDetailEvent
import com.example.drivenext.utils.NetworkConnectivity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Screen for displaying detailed car information and booking interface
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailScreen(
    carId: Long,
    userId: Long,
    viewModel: CarDetailViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToBookings: () -> Unit,
    onShowError: (String) -> Unit,
    onShowSuccess: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val isConnected by NetworkConnectivity.connectivityState()
    val scrollState = rememberScrollState()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    // Load car data when the screen is first displayed
    LaunchedEffect(carId) {
        viewModel.setEvent(CarDetailEvent.LoadCar(carId))
    }
    
    // Handle UI effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CarDetailViewModel.CarDetailEffect.NavigateBack -> {
                    onNavigateBack()
                }
                is CarDetailViewModel.CarDetailEffect.ShowError -> {
                    onShowError(effect.message)
                }
                is CarDetailViewModel.CarDetailEffect.BookingSuccess -> {
                    onShowSuccess("Booking confirmed! Booking ID: ${effect.bookingId}")
                    onNavigateToBookings()
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
            // Заменяем SmallTopAppBar на TopAppBar, который является актуальным компонентом в Material 3
            TopAppBar(
                title = { Text("Car Details") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.setEvent(CarDetailEvent.BackPressed) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (state.car == null) {
                Text(
                    text = "Car not found",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else {
                val car = state.car!!
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Car image
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(car.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "${car.brand} ${car.model}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )
                    
                    // Car details
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "${car.brand} ${car.model}",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Year: ${car.year}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "${currencyFormat.format(car.pricePerDay)} / day",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = car.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Booking section
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Book This Car",
                                    style = MaterialTheme.typography.titleLarge,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Date selection
                                DateSelectionItem(
                                    label = "Start Date",
                                    date = state.startDate,
                                    onDateSelected = { viewModel.setEvent(CarDetailEvent.StartDateSelected(it)) }
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                DateSelectionItem(
                                    label = "End Date",
                                    date = state.endDate,
                                    onDateSelected = { viewModel.setEvent(CarDetailEvent.EndDateSelected(it)) }
                                )
                                
                                // Show date error if any
                                state.dateError?.let { errorMessage ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = errorMessage,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Show total price if dates are selected
                                if (state.startDate != null && state.endDate != null && state.totalPrice > 0) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Total Price:",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = currencyFormat.format(state.totalPrice),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    val startDateStr = dateFormat.format(state.startDate)
                                    val endDateStr = dateFormat.format(state.endDate)
                                    Text(
                                        text = "From $startDateStr to $endDateStr",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Book button
                                Button(
                                    onClick = { viewModel.setEvent(CarDetailEvent.BookCar(userId)) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    enabled = !state.bookingInProgress && 
                                             state.startDate != null && 
                                             state.endDate != null && 
                                             state.dateError == null
                                ) {
                                    if (state.bookingInProgress) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    } else {
                                        Text("Book Now")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Composable for date selection with a simple date picker
 */
@Composable
fun DateSelectionItem(
    label: String,
    date: Date?,
    onDateSelected: (Date) -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    var showDatePicker by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        
        OutlinedButton(
            onClick = { showDatePicker = true }
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Select Date"
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(date?.let { dateFormat.format(it) } ?: "Select")
        }
    }
    
    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        date?.let { calendar.time = it }
        
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    onDateSelected(calendar.time)
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Select Date") },
            text = {
                // Simple date picker UI
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    // This would typically be a proper date picker UI
                    // For simplicity, let's just increment the date by a day
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dateFormat.format(calendar.time),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Row {
                            IconButton(onClick = {
                                calendar.add(Calendar.DAY_OF_MONTH, -1)
                            }) {
                                Text("-")
                            }
                            IconButton(onClick = {
                                calendar.add(Calendar.DAY_OF_MONTH, 1)
                            }) {
                                Text("+")
                            }
                        }
                    }
                }
            }
        )
    }
}