package com.example.baki_tracker.nutrition.history.details

import com.example.baki_tracker.model.nutrition.FoodItem

data class HistoryDetailsUiState(val trackingDayId: String="", val date: String ="", val foodItems: List<FoodItem> = emptyList(), val selectedFoodItem: FoodItem?= null)
