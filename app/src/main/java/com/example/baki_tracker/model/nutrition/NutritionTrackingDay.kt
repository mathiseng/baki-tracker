package com.example.baki_tracker.model.nutrition

import com.google.firebase.Timestamp

data class NutritionTrackingDay(
    var uuid: String = "",
    var date: Timestamp = Timestamp.now(),
    var nutritionEntries: List<NutritionEntry> = emptyList()
)
