package com.example.baki_tracker.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baki_tracker.model.nutrition.FoodItem
import com.example.baki_tracker.model.nutrition.NutritionTrackingDay
import com.example.baki_tracker.repository.INutritionRepository
import com.example.baki_tracker.repository.NutritionRequestState
import com.example.baki_tracker.utils.formatTimestampToString
import com.example.baki_tracker.utils.getCurrentDateString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class NutritionViewModel(
    private val nutritionRepository: INutritionRepository,
    private val sharedNutritionStateRepository: ISharedNutritionStateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NutritionUiState.initialUiState())
    val uiState: StateFlow<NutritionUiState> = _uiState.asStateFlow()


    init {
        //fetchCurrentDayAndHistory()
        viewModelScope.launch {
            nutritionRepository.getNutritionTrackingDays()
        }

        viewModelScope.launch {
            nutritionRepository.nutritionRequestState.collect { requestState ->
                when (requestState) {
                    is NutritionRequestState.Error -> _uiState.update {
                        it.copy(
                            errorMessage = requestState.message, isLoading = false
                        )
                    }

                    is NutritionRequestState.Results -> _uiState.update {
                        it.copy(
                            searchResults = requestState.results,
                            isLoading = false,
                            errorMessage = null
                        )
                    }

                    is NutritionRequestState.SingleResult -> _uiState.update {
                        it.copy(
                            showBarcodeScanner = false,
                            selectedFoodItem = requestState.foodItem,
                            isLoading = false,
                            errorMessage = null,
                        )
                    }

                    is NutritionRequestState.Loading -> _uiState.update {
                        it.copy(
                            isLoading = true, errorMessage = null
                        )
                    }

                    is NutritionRequestState.Idle -> {}
                }
            }
        }

        viewModelScope.launch {
            nutritionRepository.nutritionTrackingDays.collect { trackingDays ->
                val currentDay = getCurrentDateString()

                _uiState.update {
                    it.copy(
                        history = trackingDays.filterNot { trackingDay -> trackingDay.date.formatTimestampToString() == currentDay },
                        today = trackingDays.firstOrNull { trackingDay -> trackingDay.date.formatTimestampToString() == currentDay },
                        selectedFoodItem = null,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateSearchText(text: String) {
        _uiState.update { it.copy(searchText = text) }
    }


    fun updateSearchResults(foodItems: List<FoodItem>) {
        _uiState.update { currentState ->
            currentState.copy(searchResults = foodItems, isLoading = false, errorMessage = null)
        }
    }

    fun saveFoodItemToDay(foodItem: FoodItem) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            nutritionRepository.addFoodItemToTrackingDay(
                foodItem = foodItem, trackingDayId = getCurrentDateString()
            )
        }
    }

    fun searchFoodBarcode(barcode: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        nutritionRepository.fetchProductByBarcode(barcode)
    }

    fun onShowBarcodeScannerChange(show: Boolean) {
        _uiState.update { it.copy(showBarcodeScanner = show) }
    }

    fun onFoodItemSelectionChange(foodItem: FoodItem?) {
        _uiState.update { it.copy(selectedFoodItem = foodItem) }
    }

    fun onDetailsClick(trackingDay: NutritionTrackingDay) {
        sharedNutritionStateRepository.updateSelectedTrackingDay(trackingDay)
        sharedNutritionStateRepository.updateSelectedBottomSheet(NutritionBottomSheet.DETAILS)
    }

    fun searchFood() {
        val query = uiState.value.searchText
        if (query.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Search query cannot be empty") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            nutritionRepository.searchFood(query)
        }
    }
}
