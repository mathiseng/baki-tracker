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
import com.example.baki_tracker.nutrition.NutritionSummary


@Composable
fun NutritionHistoryCard(summary: NutritionSummary, date: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = date, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                NutrientCircle("Carbs", summary.carbs)
                NutrientCircle("Fat", summary.fat)
                NutrientCircle("Protein", summary.protein)
                NutrientCircle("Kcal", summary.kcal)
            }

            Spacer(modifier = Modifier.height(16.dp))

            summary.details.forEach { (name, quantity) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = name, fontSize = 14.sp, color = Color.Black)
                    Text(text = "$quantity g", fontSize = 14.sp, color = Color.Gray)
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
    // Example summary data
    val sampleSummary = NutritionSummary(
        carbs = 120,
        fat = 40,
        protein = 75,
        kcal = 1500,
        micronutrients = mapOf(
            "Vitamin C" to 20.0f,
            "Iron" to 10.0f
        ),
        details = listOf(
            "Apple" to 200,
            "Chicken Breast" to 150,
            "Rice" to 250
        )
    )

    NutritionHistoryCard(
        summary = sampleSummary,
        date = "2025-01-14"
    )
}


