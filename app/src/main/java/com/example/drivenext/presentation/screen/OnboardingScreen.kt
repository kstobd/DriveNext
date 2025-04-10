package com.example.drivenext.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.drivenext.R
import com.example.drivenext.domain.model.OnboardingItem
import com.example.drivenext.presentation.viewmodel.OnboardingViewModel
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

/**
 * Экран онбординга для отображения преимуществ приложения
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnboardingScreen(
    onBoardingFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val items = viewModel.onboardingItems
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.onboarding_background))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Skip Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        viewModel.completeOnboarding()
                        onBoardingFinished()
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.skip),
                        color = colorResource(id = R.color.onboarding_blue),
                        fontSize = 16.sp
                    )
                }
            }
            
            // Pager
            HorizontalPager(
                count = items.size,
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                OnboardingPage(items[page])
            }
            
            // Indicators and buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                // Page indicators
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(bottom = 64.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(items.size) { position ->
                        val isSelected = position == pagerState.currentPage
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (isSelected) 12.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    color = if (isSelected) 
                                        colorResource(id = R.color.indicator_active) 
                                    else 
                                        colorResource(id = R.color.indicator_inactive)
                                )
                        )
                    }
                }
                
                // Next or Get Started Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isLastPage = pagerState.currentPage == items.size - 1
                    Button(
                        onClick = {
                            if (isLastPage) {
                                viewModel.completeOnboarding()
                                onBoardingFinished()
                            } else {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.button_primary)
                        )
                    ) {
                        Text(
                            text = if (isLastPage) 
                                stringResource(id = R.string.get_started) 
                            else 
                                stringResource(id = R.string.next),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Страница онбординга с изображением, заголовком и описанием
 */
@Composable
fun OnboardingPage(item: OnboardingItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(300.dp)
                .padding(bottom = 32.dp, top = 32.dp)
        )
        
        Text(
            text = stringResource(id = item.titleRes),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.text_primary),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(id = item.descriptionRes),
            fontSize = 16.sp,
            color = colorResource(id = R.color.text_secondary),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}