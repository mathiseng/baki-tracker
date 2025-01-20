package com.example.baki_tracker.nutrition.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.baki_tracker.nutrition.NutritionViewModel
import com.example.baki_tracker.utils.formatTimestampToString
import me.tatarka.inject.annotations.Inject

typealias HistoryScreen = @Composable () -> Unit

@Inject
@Composable
fun HistoryScreen(nutritionViewModel: () -> NutritionViewModel) {
    val viewModel = viewModel { nutritionViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        // Section for "Today"
        Text(
            text = "Today",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Light,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(vertical = 16.dp)
        )

        // Show summary card for today's data if available
        uiState.today?.let { today ->
            if (today.food.isNotEmpty()) {
                FoodSummaryCard(
                    nutritionTrackingDay = today,
                    calorieGoal = 2000, // Replace with dynamic goal if available
                    proteinGoal = 150,
                    carbsGoal = 200,
                    fatsGoal = 70
                )
            } else {
                Text(
                    text = "No data for today",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
        } ?: run {
            Text(
                text = "Loading...",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.outline,
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        Column(modifier = Modifier.fillMaxSize()) {

            uiState.history.forEach { historyDay ->
                Text(
                    text = historyDay.date.formatTimestampToString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                NutritionHistoryCard(historyDay)
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

