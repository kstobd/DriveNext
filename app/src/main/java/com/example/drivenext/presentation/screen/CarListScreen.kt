package com.example.drivenext.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.drivenext.domain.model.Car
import com.example.drivenext.presentation.viewmodel.CarListViewModel
import com.example.drivenext.presentation.viewmodel.CarListViewModel.CarListEvent
import com.example.drivenext.utils.NetworkConnectivity
import java.text.NumberFormat
import java.util.*

/**
 * Screen that displays a list of available cars for rent
 */
@Composable
fun CarListScreen(
    viewModel: CarListViewModel = hiltViewModel(),
    onNavigateToCarDetail: (Long) -> Unit,
    onShowError: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val isConnected by NetworkConnectivity.connectivityState()

    // Handle UI effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CarListViewModel.CarListEffect.NavigateToCarDetail -> {
                    onNavigateToCarDetail(effect.carId)
                }
                is CarListViewModel.CarListEffect.ShowError -> {
                    onShowError(effect.message)
                }
            }
        }
    }

    if (!isConnected) {
        NoConnectionScreen {
            viewModel.setEvent(CarListEvent.RefreshCars)
        }
        return
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.isLoading && state.cars.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (state.cars.isEmpty() && state.error == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No cars available",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Please check back later",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.setEvent(CarListEvent.RefreshCars) }
                ) {
                    Text("Refresh")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.cars) { car ->
                    CarItem(
                        car = car,
                        onClick = { viewModel.setEvent(CarListEvent.CarSelected(car)) }
                    )
                }
            }
        }
    }
}

/**
 * Individual car item in the list
 */
@Composable
fun CarItem(
    car: Car,
    onClick: () -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(car.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "${car.brand} ${car.model}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "${car.brand} ${car.model}",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Year: ${car.year}",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${currencyFormat.format(car.pricePerDay)} / day",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = car.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
            }
        }
    }
}