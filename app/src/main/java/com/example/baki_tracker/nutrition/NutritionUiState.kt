package com.example.baki_tracker.nutrition

import com.example.baki_tracker.model.nutrition.FoodItem
import com.example.baki_tracker.model.nutrition.NutritionTrackingDay

data class NutritionUiState(
    val searchText: String = "",
    val searchResults: List<FoodItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedFoodItem: FoodItem? = null,
    val showBarcodeScanner: Boolean = false,
    val today: NutritionTrackingDay?,
    val history: List<NutritionTrackingDay>
)
{
   companion object {
       fun initialUiState() = NutritionUiState(
           "", emptyList(), false, null, null, false, null,  emptyList()
       )
   }
}