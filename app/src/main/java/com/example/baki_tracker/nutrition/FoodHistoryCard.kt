package com.example.baki_tracker.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
fun FoodHistoryCard(
    date: String,
    caloriesConsumed: Int,
    nutrients: Map<String, Map<String, String>> // Nested map to include subcategories and their values
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8EAF6)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE8EAF6))
                .padding(16.dp)
        ) {
            // Date and Calories Consumed
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = date,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "$caloriesConsumed kcal",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray
                )
            }
            //Categories + Subcategories
            Spacer(modifier = Modifier.height(8.dp))
            nutrients.forEach { (category, subcategories) ->
                if (category == "Protein") {
                    Text(
                        text = "$category - ${subcategories.values.sumOf { it.toInt() }}g",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                } else {
                    Text(
                        text = "$category - ${subcategories.values.sumOf { it.toInt() }}g",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    subcategories.forEach { (subcategory, value) ->
                        Text(
                            text = "• $subcategory: $value g",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFoodHistoryCard() {
    Column {
        FoodHistoryCard(
            date = "27.10.2024",
            caloriesConsumed = 1200,
            nutrients = mapOf(
                "Fats" to mapOf(
                    "Saturated Fat" to "15",
                    "Trans Fat" to "5",
                    "Other" to "20"
                ),
                "Carbohydrates" to mapOf("Sugar" to "40", "Fiber" to "60"),
                "Protein" to mapOf("" to "70")
            )
        )
        FoodHistoryCard(
            date = "28.10.2024",
            caloriesConsumed = 1400,
            nutrients = mapOf(
                "Fats" to mapOf(
                    "Saturated Fat" to "10",
                    "Trans Fat" to "4",
                    "Other" to "15"
                ),
                "Carbohydrates" to mapOf("Sugar" to "50", "Fiber" to "50"),
                "Protein" to mapOf("" to "80")
            )
        )
        FoodHistoryCard(
            date = "29.10.2024",
            caloriesConsumed = 1500,
            nutrients = mapOf(
                "Fats" to mapOf(
                    "Saturated Fat" to "18",
                    "Trans Fat" to "6",
                    "Other" to "25"
                ),
                "Carbohydrates" to mapOf("Sugar" to "30", "Fiber" to "40"),
                "Protein" to mapOf("" to "90")
            )
        )
    }
}


