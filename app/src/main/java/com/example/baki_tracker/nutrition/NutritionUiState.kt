package com.example.baki_tracker.nutrition

data class NutritionUiState(
    val searchText: String = "",
    val searchResults: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val today: Day?,
    val history: List<Day>
)
{
   companion object {
       fun initialUiState() = NutritionUiState(
           "", emptyList(), false, null, null, emptyList()
       )
   }
}


data class FoodItem(
   val uuid: Int,
    val name: String,
    val calories: Float,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val quantity: String,
    val micronutrients: Map<String, Float>
)

data class NutritionSummary(
    val carbs: Int,
    val fat: Int,
    val protein: Int,
    val kcal: Int,
    val micronutrients: Map<String, Float>,
    val details: List<Pair<String, Int>> // Food item name and quantity
)


//Represents a Day of the user's nutrition history (i.e. one NutritionHistoryCard)
data class Day(
    val uuid: Int,
    val date: String,
    val food: List<FoodItem>
)