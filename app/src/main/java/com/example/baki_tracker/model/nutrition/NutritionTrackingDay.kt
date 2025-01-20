package com.example.baki_tracker.model.nutrition

import com.example.baki_tracker.utils.getCurrentDateString
import com.google.firebase.Timestamp
import java.util.UUID

//Represents a NutritionTrackingDay of the user's nutrition history (i.e. one NutritionHistoryCard)
data class NutritionTrackingDay(
    var uuid: String = getCurrentDateString(),
    val date: Timestamp = Timestamp.now(),
    var foodItems: List<FoodItem> = emptyList()
)