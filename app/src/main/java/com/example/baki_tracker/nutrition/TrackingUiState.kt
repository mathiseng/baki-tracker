package com.example.baki_tracker.nutrition

data class TrackingUiState(
    val searchText: String = "",
    val searchResults: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // food history test for now
    val todayCarbs: Int = 0,
    val todayFat: Int = 0,
    val todayProtein: Int = 0,
    val todayCalories: Int = 0,
    val todayDetails: List<Pair<String, Int>> = emptyList(),
    val history: List<HistoryItem> = emptyList()
)
{
   companion object {
       fun initialUiState() = TrackingUiState("", emptyList(), false, null)
   }
}
data class HistoryItem(
    val date: String,
    val carbs: Int,
    val fat: Int,
    val protein: Int,
    val calories: Int,
    val details: List<Pair<String, Int>>
)