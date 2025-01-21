package com.example.baki_tracker.nutrition.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baki_tracker.model.nutrition.NutritionTrackingDay
import com.example.baki_tracker.model.nutrition.FoodItem
import com.google.firebase.Timestamp


@Composable
fun NutritionHistoryCard(nutritionTrackingDay: NutritionTrackingDay, onDetailsClick: () -> Unit) {
    val totalCarbs = nutritionTrackingDay.foodItems.sumOf { (it.carbs * it.quantity).toInt() }
    val totalFat = nutritionTrackingDay.foodItems.sumOf { (it.fat * it.quantity).toInt() }
    val totalProtein = nutritionTrackingDay.foodItems.sumOf { (it.protein * it.quantity).toInt() }
    val totalCalories = nutritionTrackingDay.foodItems.sumOf { (it.calories * it.quantity).toInt() }

    Card( elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),modifier = Modifier.fillMaxWidth().clickable { onDetailsClick() }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                NutrientCircle("Carbs", totalCarbs)
                NutrientCircle("Fat", totalFat)
                NutrientCircle("Protein", totalProtein)
                NutrientCircle("Kcal", totalCalories)
            }

            Spacer(modifier = Modifier.height(16.dp))

            nutritionTrackingDay.foodItems.forEach { foodItem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = foodItem.name, fontSize = 14.sp)
                    Text(
                        text = "${foodItem.quantity} g",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
fun NutrientCircle(label: String, value: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .size(55.dp)
                .background(
                    color = MaterialTheme.colorScheme.onPrimaryContainer, shape = CircleShape
                )
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )
        }
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUpdatedNutritionHistoryCard() {
    val sampleNutritionTrackingDay = NutritionTrackingDay(
        date = Timestamp.now(), foodItems = listOf(
            FoodItem(
                name = "Apple",
                carbs = 25f,
                fat = 0.5f,
                protein = 0.3f,
                calories = 95f,
                quantity = 150f
            ), FoodItem(
                name = "Chicken Breast",
                carbs = 0f,
                fat = 3.5f,
                protein = 31f,
                calories = 165f,
                quantity = 100f
            ), FoodItem(
                name = "Rice",
                carbs = 28f,
                fat = 0.2f,
                protein = 2.7f,
                calories = 130f,
                quantity = 150f
            )
        )
    )

    NutritionHistoryCard(nutritionTrackingDay = sampleNutritionTrackingDay,{})
}