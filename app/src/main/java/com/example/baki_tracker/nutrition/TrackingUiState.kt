package com.example.baki_tracker.nutrition

data class TrackingUiState(
    val searchText: String = "",
    val searchResults: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
{
   companion object {
       fun initialUiState() = TrackingUiState("", emptyList(), false, null)
   }
}