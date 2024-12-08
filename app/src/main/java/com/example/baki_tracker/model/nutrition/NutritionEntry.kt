package com.example.baki_tracker.model.nutrition

import java.util.UUID

data class NutritionEntry(
    var uuid: String = UUID.randomUUID().toString(),
    var name: String = "",
    var calories: Double = 0.0,
    val proteins: Double = 0.0,
    var carbs: Double = 0.0,
    var fats: Double = 0.0,
    var portionSize: String = "", //z.B. "100g", "1 Stück", "1 Tasse"
    var quantity: Double = 0.0
)
