package com.example.baki_tracker.nutrition.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailModalBottomSheet(
    date: String,
    caloriesConsumed: Int,
    nutrients: Map<String, Map<String, String>> // Nested map to include subcategories and their values
) {
    val bottomSheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    // Modal Bottom Sheet
    ModalBottomSheet(
        onDismissRequest = { coroutineScope.launch { bottomSheetState.hide() } },
        sheetState = bottomSheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = Color(0xFFE8EAF6)
    ) {
        // Content of the bottom sheet
        Column(
            modifier = Modifier
                .fillMaxWidth()
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

            Spacer(modifier = Modifier.height(8.dp))

            // Categories + Subcategories
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

    // Button to trigger the Bottom Sheet
    Button(
        onClick = { coroutineScope.launch { bottomSheetState.show() } },
        modifier = Modifier.padding(16.dp),

    ) {
        Text(text = "Show History Details",
            color=Color.Black)

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewHistoryDetailModalBottomSheet() {
    HistoryDetailModalBottomSheet(
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
}