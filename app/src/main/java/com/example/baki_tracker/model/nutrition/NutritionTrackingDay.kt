package com.example.baki_tracker.model.nutrition

import com.google.firebase.Timestamp
import java.util.UUID

//Represents a NutritionTrackingDay of the user's nutrition history (i.e. one NutritionHistoryCard)
data class NutritionTrackingDay(
    val uuid: String = UUID.randomUUID().toString(),
    val date: Timestamp = Timestamp.now(),
    val food: List<FoodItem> = emptyList()
)