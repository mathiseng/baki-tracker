package com.example.baki_tracker.nutrition.history

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baki_tracker.model.nutrition.FoodItem
import com.example.baki_tracker.model.nutrition.NutritionTrackingDay
import com.google.firebase.Timestamp

@Composable
fun FoodSummaryCard(
    modifier: Modifier = Modifier,
    foodItems: List<FoodItem>,
    calorieGoal: Int,
    proteinGoal: Int,
    carbsGoal: Int,
    fatsGoal: Int
) {
    // Calculate consumed values from the nutritionTrackingDay's food items
    val caloriesConsumed = foodItems.sumOf { (it.calories * it.quantity).toInt() }
    val proteinConsumed = foodItems.sumOf { (it.protein * it.quantity).toInt() }
    val carbsConsumed = foodItems.sumOf { (it.carbs * it.quantity).toInt() }
    val fatsConsumed = foodItems.sumOf { (it.fat * it.quantity).toInt() }
    val caloriesBurned = 200 // Static test value

    Card( elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Consumed",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.outline,
                    )
                    Text("$caloriesConsumed kcal", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                DonutChart(
                    caloriesConsumed = caloriesConsumed,
                    caloriesBurned = caloriesBurned,
                    calorieGoal = calorieGoal
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Burned",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.outline,
                    )
                    Text("$caloriesBurned kcal", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NutrientBar(
                    label = "Protein",
                    consumed = proteinConsumed,
                    goal = proteinGoal,
                    consumedColor = Color(0xFF4CAF50)
                )
                NutrientBar(
                    label = "Carbs",
                    consumed = carbsConsumed,
                    goal = carbsGoal,
                    consumedColor = Color(0xFFFFC107)
                )
                NutrientBar(
                    label = "Fats",
                    consumed = fatsConsumed,
                    goal = fatsGoal,
                    consumedColor = Color(0xFF03A9F4)
                )
            }
        }
    }
}

@Composable
fun DonutChart(
    caloriesConsumed: Int,
    caloriesBurned: Int,
    calorieGoal: Int
) {
    val totalCalories = caloriesConsumed - caloriesBurned
    val consumedAngle = (caloriesConsumed.toFloat() / calorieGoal) * 360
    val burnedAngle = (caloriesBurned.toFloat() / calorieGoal) * 360


    val remainingKcal = (calorieGoal - totalCalories).coerceAtLeast(0)

    Box(
        contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)
    ) {
        // Donut chart canvas
        val baseColor = MaterialTheme.colorScheme.outlineVariant
        val burnedColor = MaterialTheme.colorScheme.error
        val consumedColor = MaterialTheme.colorScheme.primary

        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 30f
            drawArc(
                color = baseColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
                color = burnedColor,
                startAngle = -90f,
                sweepAngle = burnedAngle,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
                color = consumedColor,
                startAngle = -90f + burnedAngle,
                sweepAngle = consumedAngle,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Remaining kcal text in the center
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$remainingKcal kcal",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "remaining", fontSize = 14.sp, color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun NutrientBar(label: String, consumed: Int, goal: Int, consumedColor: Color) {
    val consumedRatio = if (goal > 0) (consumed.toFloat() / goal).coerceIn(0f, 1f) else 0f
    val remainingRatio = if (goal > 0) 1f - consumedRatio else 0f


    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label $consumed/$goal g",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        ) {

            LinearProgressIndicator(progress = { consumedRatio / (consumedRatio + remainingRatio) },
                trackColor = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = consumedColor,
                strokeCap = StrokeCap.Round,
                gapSize = (-15).dp,
                drawStopIndicator = {})
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFoodSummaryCard() {
    val sampleNutritionTrackingDay = NutritionTrackingDay(
        date = Timestamp.now(), foodItems = listOf(
            FoodItem(
                name = "Apple",
                calories = 0.52f,
                protein = 0.003f,
                carbs = 0.14f,
                fat = 0.002f,
                quantity = 150f
            ), FoodItem(
                name = "Egg",
                calories = 1.43f,
                protein = 0.12f,
                carbs = 0.01f,
                fat = 0.1f,
                quantity = 50f
            )
        )
    )

    FoodSummaryCard(
        foodItems = sampleNutritionTrackingDay.foodItems,
        calorieGoal = 2000,
        proteinGoal = 150,
        carbsGoal = 200,
        fatsGoal = 70
    )
}

