package com.example.baki_tracker.nutrition.tracking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.baki_tracker.model.nutrition.FoodItem

@Composable
fun FoodItemCard(
    foodItem: FoodItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = foodItem.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.W500
            )
            Text(
                text = "100g - ${foodItem.calories * 100} kcal",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
