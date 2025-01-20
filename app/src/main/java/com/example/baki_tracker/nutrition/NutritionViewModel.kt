package com.example.baki_tracker.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baki_tracker.model.nutrition.FoodItem
import com.example.baki_tracker.repository.INutritionRepository
import com.example.baki_tracker.repository.NutritionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class NutritionViewModel(private val nutritionRepository: INutritionRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(NutritionUiState.initialUiState())
    val uiState: StateFlow<NutritionUiState> = _uiState.asStateFlow()


    init {
        fetchCurrentDayAndHistory()
        observeHistory()
    }

    private fun observeHistory() {
        nutritionRepository.observeUserHistory(
            onUpdate = { days ->
                _uiState.update { currentState ->
                    currentState.copy(history = days)
                }
            },
            onError = { exception ->
                _uiState.update { currentState ->
                    currentState.copy(errorMessage = exception.message)
                }
            }
        )
    }

    private fun fetchCurrentDayAndHistory() {
        viewModelScope.launch {
            try {
                val currentDay = nutritionRepository.fetchCurrentDay()
                val userHistory = nutritionRepository.fetchHistory()

                _uiState.update {
                    currentState -> currentState.copy(today = currentDay, history = userHistory)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
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

        nutritionRepository.saveFoodItemToDay(
            foodItem = foodItem,
            onSuccess = {
                _uiState.update { it.copy(isLoading = false, errorMessage = null, selectedFoodItem = null)}
            },
            onFailure = { exception ->
                _uiState.update { it.copy(isLoading = false, errorMessage = exception.message) }
            }
        )
    }

    fun searchFoodBarcode(barcode: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        nutritionRepository.fetchProductByBarcode(
            barcode,
            onSuccess =  {
                foodItem ->
                _uiState.update { it.copy(selectedFoodItem = foodItem, isLoading = false, errorMessage = null, showBarcodeScanner = false) }
            },
            onError = { exception ->
                _uiState.update { it.copy(isLoading = false, errorMessage = exception.message) }
        }
        )
    }

    fun onShowBarcodeScannerChange(show: Boolean) {
        _uiState.update { it.copy(showBarcodeScanner = show) }
    }

    fun onFoodItemSelectionChange(foodItem: FoodItem?) {
        _uiState.update { it.copy(selectedFoodItem = foodItem) }
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
            nutritionRepository.nutritionState.collect { state ->
                when (state) {
                    is NutritionState.Results -> {
                        updateSearchResults(state.results)
                    }
                    is NutritionState.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = state.message) }
                    }
                    else -> { /* Handle other states if necessary */ }
                }
            }
        }
    }

}
