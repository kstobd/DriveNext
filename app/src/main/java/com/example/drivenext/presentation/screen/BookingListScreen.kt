package com.example.drivenext.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.drivenext.domain.model.BookingStatus
import com.example.drivenext.presentation.screen.NoConnectionScreen
import com.example.drivenext.presentation.util.LocalNetworkConnectivity
import com.example.drivenext.presentation.viewmodel.BookingListViewModel
import com.example.drivenext.presentation.viewmodel.BookingListViewModel.BookingListEvent
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Screen for displaying a user's bookings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingListScreen(
    userId: Long,
    viewModel: BookingListViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToBookingDetail: (Long) -> Unit,
    onShowError: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    
    // Получаем NetworkConnectivity через CompositionLocal
    val networkConnectivity = LocalNetworkConnectivity.current
    val isConnected by networkConnectivity.observeNetworkStatus().collectAsState(initial = true)
    
    // Load bookings when the screen is first displayed
    LaunchedEffect(userId) {
        viewModel.setEvent(BookingListEvent.LoadBookings(userId))
    }
    
    // Handle UI effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BookingListViewModel.BookingListEffect.NavigateToBookingDetail -> {
                    onNavigateToBookingDetail(effect.bookingId)
                }
                is BookingListViewModel.BookingListEffect.ShowError -> {
                    onShowError(effect.message)
                }
            }
        }
    }
    
    // Проверяем подключение к интернету
    if (!isConnected) {
        NoConnectionScreen(
            onRetry = { viewModel.setEvent(BookingListEvent.RefreshBookings) }
        )
        return
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Bookings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
            if (state.isLoading && state.bookings.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (state.bookings.isEmpty() && state.error == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No bookings found",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You haven't made any bookings yet",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.bookings) { bookingWithCar ->
                        BookingItem(
                            bookingWithCar = bookingWithCar,
                            onClick = { 
                                viewModel.setEvent(BookingListEvent.BookingSelected(bookingWithCar.booking.id))
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual booking item in the list
 */
@Composable
fun BookingItem(
    bookingWithCar: BookingListViewModel.BookingWithCar,
    onClick: () -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val booking = bookingWithCar.booking
    val car = bookingWithCar.car
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Car image if available
            if (car != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(car.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "${car.brand} ${car.model}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(120.dp)
                        .fillMaxHeight()
                )
            } else {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                // Car details if available
                if (car != null) {
                    Text(
                        text = "${car.brand} ${car.model}",
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = "Loading car details...",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Booking dates
                Text(
                    text = "From: ${dateFormat.format(booking.startDate)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Text(
                    text = "To: ${dateFormat.format(booking.endDate)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Booking status
                BookingStatusChip(status = booking.status)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Total price
                Text(
                    text = "Total: ${currencyFormat.format(booking.totalPrice)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Chip displaying booking status with appropriate color
 */
@Composable
fun BookingStatusChip(status: BookingStatus) {
    val (backgroundColor, contentColor) = when (status) {
        BookingStatus.PENDING -> Pair(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), MaterialTheme.colorScheme.primary)
        BookingStatus.CONFIRMED -> Pair(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f), MaterialTheme.colorScheme.tertiary)
        BookingStatus.CANCELLED -> Pair(MaterialTheme.colorScheme.error.copy(alpha = 0.1f), MaterialTheme.colorScheme.error)
        BookingStatus.COMPLETED -> Pair(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), MaterialTheme.colorScheme.secondary)
    }
    
    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.wrapContentSize()
    ) {
        Text(
            text = status.name,
            color = contentColor,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}