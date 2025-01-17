package com.example.baki_tracker.nutrition.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baki_tracker.nutrition.Day
import com.example.baki_tracker.nutrition.FoodItem

@Composable
fun FoodSummaryCard(
    day: Day,
    calorieGoal: Int,
    proteinGoal: Int,
    carbsGoal: Int,
    fatsGoal: Int
) {
    // Calculate consumed values from the day's food items
    val caloriesConsumed = day.food.sumOf { (it.calories * it.quantity).toInt() }
    val proteinConsumed = day.food.sumOf { (it.protein * it.quantity).toInt() }
    val carbsConsumed = day.food.sumOf { (it.carbs * it.quantity).toInt() }
    val fatsConsumed = day.food.sumOf { (it.fat * it.quantity).toInt() }
    val caloriesBurned = 400 // Static test value

    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F0F8))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Consumed", fontSize = 14.sp, color = Color.Gray,
                        modifier = Modifier.padding(8.dp))
                    Text("$caloriesConsumed kcal", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Burned", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(8.dp))
                    Text("$caloriesBurned kcal", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(180.dp)
                    .padding(8.dp)
            ) {
                DonutChart(
                    caloriesConsumed = caloriesConsumed,
                    caloriesBurned = caloriesBurned,
                    calorieGoal = calorieGoal
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NutrientBar(label = "Protein", consumed = proteinConsumed, goal = proteinGoal, consumedColor = Color(0xFF4CAF50))
                NutrientBar(label = "Carbs", consumed = carbsConsumed, goal = carbsGoal, consumedColor = Color(0xFFFFC107))
                NutrientBar(label = "Fats", consumed = fatsConsumed, goal = fatsGoal, consumedColor = Color(0xFF03A9F4))
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
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(200.dp)
    ) {
        // Donut chart canvas
        Canvas(modifier = Modifier.size(200.dp)) {
            val strokeWidth = 30f

            drawArc(
                color = Color.LightGray,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
                color = Color.Blue,
                startAngle = -90f,
                sweepAngle = burnedAngle,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
                color = Color.Green,
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
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "remaining",
                fontSize = 14.sp,
                color = Color.Gray
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
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        ) {
            // Consumed portion
            if (consumedRatio > 0) {
                Box(
                    modifier = Modifier
                        .weight(consumedRatio)
                        .height(8.dp)
                        .background(consumedColor, shape = RectangleShape)
                )
            }

            // Remaining portion
            if (remainingRatio > 0) {
                Box(
                    modifier = Modifier
                        .weight(remainingRatio)
                        .height(8.dp)
                        .background(Color.LightGray, shape = RectangleShape)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFoodSummaryCard() {
    val sampleDay = Day(
        uuid = 1,
        date = "2025-01-16",
        food = listOf(
            FoodItem(
                uuid = 1,
                name = "Apple",
                calories = 0.52f,
                protein = 0.003f,
                carbs = 0.14f,
                fat = 0.002f,
                quantity = 150f
            ),
            FoodItem(
                uuid = 2,
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
        day = sampleDay,
        calorieGoal = 2000,
        proteinGoal = 150,
        carbsGoal = 200,
        fatsGoal = 70
    )
}

