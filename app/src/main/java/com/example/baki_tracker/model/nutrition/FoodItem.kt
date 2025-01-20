package com.example.baki_tracker.model.nutrition

import java.util.UUID

data class FoodItem(
    val uuid: String = UUID.randomUUID().toString(),
    val name: String = "",
    val calories: Float = 0f,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,
    val quantity: Float = 0f,
    val micronutrients: Map<String, Float> = emptyMap()
)