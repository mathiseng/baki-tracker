package com.example.baki_tracker.nutrition.tracking
import android.util.Log
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
import androidx.compose.material3.Button
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


@Composable
fun NutritionHistoryCard(
    carbs: Int,
    fat: Int,
    protein: Int,
    kcal: Int,
    details: List<Pair<String, Int>>) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Top Row: Nutrient Circles
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                NutrientCircle("Carbs", carbs)
                NutrientCircle("Fat", fat)
                NutrientCircle("Protein", protein)
                NutrientCircle("Kcal", kcal)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Details Section
            details.forEach { (name, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = name,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Text(
                        text = value.toString(),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Show More Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) { Button(
                onClick = { Log.d("myapp","button clicked") }
            ) {
                Text("Show More")
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
fun PreviewNutritionHistoryCard(){
    Column {
        NutritionHistoryCard(
            carbs = 23,
            fat = 14,
            protein = 110,
            kcal = 1504,
            details = listOf(
                "Ballaststoffe" to 56,
                "Zucker" to 40,
                "Gesättigte Fettsäuren" to 14
            )
        )
    }
    Column {
        NutritionHistoryCard(
            carbs = 23,
            fat = 14,
            protein = 110,
            kcal = 1504,
            details = listOf(
                "Ballaststoffe" to 56,
                "Zucker" to 40,
                "Gesättigte Fettsäuren" to 14
            )
        )
    }
}




