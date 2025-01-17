package com.example.baki_tracker.nutrition

data class NutritionUiState(
    val searchText: String = "",
    val searchResults: List<FoodItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedFoodItem: FoodItem? = null,
    val showBarcodeScanner: Boolean = false,
    val today: Day?,
    val history: List<Day>
)
{
   companion object {
       fun initialUiState() = NutritionUiState(
           "", emptyList(), false, null, null, false, null,  emptyList()
       )
   }
}

data class FoodItem(
    val uuid: Int = 0,
    val name: String = "",
    val calories: Float = 0f,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,
    val quantity: Float = 0f,
    val micronutrients: Map<String, Float> = emptyMap()
)

//Represents a Day of the user's nutrition history (i.e. one NutritionHistoryCard)
data class Day(
    val uuid: Int = 0,
    val date: String = "",
    val food: List<FoodItem> = emptyList()
)
