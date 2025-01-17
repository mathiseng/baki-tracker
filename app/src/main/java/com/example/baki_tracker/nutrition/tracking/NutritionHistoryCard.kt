package com.example.baki_tracker.nutrition.tracking
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baki_tracker.nutrition.Day
import com.example.baki_tracker.nutrition.FoodItem


@Composable
fun NutritionHistoryCard(day: Day) {
    val totalCarbs = day.food.sumOf { (it.carbs * it.quantity).toInt() }
    val totalFat = day.food.sumOf { (it.fat * it.quantity).toInt() }
    val totalProtein = day.food.sumOf { (it.protein * it.quantity).toInt() }
    val totalCalories = day.food.sumOf { (it.calories * it.quantity).toInt() }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                NutrientCircle("Carbs", totalCarbs)
                NutrientCircle("Fat", totalFat)
                NutrientCircle("Protein", totalProtein)
                NutrientCircle("Kcal", totalCalories)
            }

            Spacer(modifier = Modifier.height(16.dp))

            day.food.forEach { foodItem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = foodItem.name, fontSize = 14.sp, color = Color.Black)
                    Text(text = "${foodItem.quantity} g", fontSize = 14.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun NutrientCircle(label: String, value: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(60.dp)
                .background(color = Color(0xFF3E3E3E), shape = CircleShape)
        ) {
            Text(
                text = value.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUpdatedNutritionHistoryCard() {
    val sampleDay = Day(
        date = "2025-01-14",
        food = listOf(
            FoodItem(name = "Apple", carbs = 25f, fat = 0.5f, protein = 0.3f, calories = 95f, quantity = 150f),
            FoodItem(name = "Chicken Breast", carbs = 0f, fat = 3.5f, protein = 31f, calories = 165f, quantity = 100f),
            FoodItem(name = "Rice", carbs = 28f, fat = 0.2f, protein = 2.7f, calories = 130f, quantity = 150f)
        )
    )

    NutritionHistoryCard(day = sampleDay)
}