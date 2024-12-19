package com.example.baki_tracker.nutrition

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

@Composable
fun FoodSummaryCard(
    caloriesGoal: Int,
    caloriesBurned: Int,
    caloriesConsumed: Int,
    proteinConsumed: Int,
    proteinGoal: Int,
    carbsConsumed: Int,
    carbsGoal: Int,
    fatsConsumed: Int,
    fatsGoal: Int
) {


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
                    calorieGoal = caloriesGoal
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
    val consumedRatio = (consumed.toFloat() / goal).coerceIn(0f, 1f)
    val remainingRatio = 1f - consumedRatio

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
            Box(
                modifier = Modifier
                    .weight(consumedRatio)
                    .height(8.dp)
                    .background(consumedColor, shape = RectangleShape)
            )
            // Remaining portion (Red)
            Box(
                modifier = Modifier
                    .weight(remainingRatio)
                    .height(8.dp)
                    .background(Color.LightGray, shape = RectangleShape)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFoodSummaryCard() {
    FoodSummaryCard(
        caloriesGoal = 2000,
        caloriesBurned = 400,
        caloriesConsumed = 1200,
        proteinConsumed = 60,
        proteinGoal = 150,
        carbsConsumed = 100,
        carbsGoal = 200,
        fatsConsumed = 40,
        fatsGoal = 70
    )
}
