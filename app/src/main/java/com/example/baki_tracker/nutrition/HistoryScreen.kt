package com.example.baki_tracker.nutrition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import me.tatarka.inject.annotations.Inject

typealias HistoryScreen = @Composable () -> Unit

@Inject
@Composable
fun HistoryScreen(trackingViewModel: () -> TrackingViewModel) {
    val viewModel = viewModel { trackingViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()



    Column(modifier = Modifier.fillMaxSize()) {
        // Tab title
        Text(
            text = "History",
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Today's card
            item {
                FoodHistoryCard(
                    carbs = uiState.todayCarbs,
                    fat = uiState.todayFat,
                    protein = uiState.todayProtein,
                    kcal = uiState.todayCalories,
                    details = uiState.todayDetails
                )
            }

            // Previous Day's cards
            items(uiState.history) { historyItem ->
                FoodHistoryCard(
                    carbs = historyItem.carbs,
                    fat = historyItem.fat,
                    protein = historyItem.protein,
                    kcal = historyItem.calories,
                    details = historyItem.details
                )
            }
        }
    }
}

