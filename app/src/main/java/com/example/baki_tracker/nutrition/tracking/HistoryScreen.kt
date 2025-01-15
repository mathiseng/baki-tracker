package com.example.baki_tracker.nutrition.tracking

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
import com.example.baki_tracker.nutrition.Day
import com.example.baki_tracker.nutrition.NutritionSummary
import com.example.baki_tracker.nutrition.NutritionViewModel
import me.tatarka.inject.annotations.Inject

typealias HistoryScreen = @Composable () -> Unit

@Inject
@Composable
fun HistoryScreen(nutritionViewModel: () -> NutritionViewModel) {
    val viewModel = viewModel { nutritionViewModel() }
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
                uiState.today?.let { todayDay ->
                    val todaySummary = transformDayToSummary(todayDay)
                    NutritionHistoryCard(
                        summary = todaySummary,
                        date = todayDay.date
                    )
                }
            }

            // Previous Day's cards
            items(uiState.history) { historyDay ->
                val historySummary = transformDayToSummary(historyDay)
                NutritionHistoryCard(
                    summary = historySummary,
                    date = historyDay.date
                )
            }
        }
    }
}

// Transformation function to calculate NutritionSummary from a Day
fun transformDayToSummary(day: Day): NutritionSummary {
    val totalCarbs = day.food.sumOf { it.carbs * it.quantity.toFloat() }
    val totalFat = day.food.sumOf { it.fat * it.quantity.toFloat() }
    val totalProtein = day.food.sumOf { it.protein * it.quantity.toFloat() }
    val totalCalories = day.food.sumOf { it.calories * it.quantity.toFloat() }

    val micronutrientTotals = day.food.flatMap { it.micronutrients.entries }
        .groupingBy { it.key }
        .fold(0f) { acc, entry -> acc + entry.value }

    val foodDetails = day.food.map { it.name to (it.quantity.toFloat() * 100).toInt() } // Assuming quantity is in 100g units.

    return NutritionSummary(
        carbs = totalCarbs.toInt(),
        fat = totalFat.toInt(),
        protein = totalProtein.toInt(),
        kcal = totalCalories.toInt(),
        micronutrients = micronutrientTotals,
        details = foodDetails
    )
}
