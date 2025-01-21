package com.example.baki_tracker.nutrition.history.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baki_tracker.R
import com.example.baki_tracker.model.nutrition.FoodItem
import com.example.baki_tracker.nutrition.ISharedNutritionStateRepository
import com.example.baki_tracker.repository.INutritionRepository
import com.example.baki_tracker.utils.formatTimestampToString
import com.example.baki_tracker.workout.components.DialogInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class HistoryDetailsViewModel(
    private val nutritionRepository: INutritionRepository,
    private val sharedNutritionStateRepository: ISharedNutritionStateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryDetailsUiState())
    val uiState: StateFlow<HistoryDetailsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sharedNutritionStateRepository.selectedTrackingDay.collect { selectedDay ->
                if (selectedDay != null) _uiState.update {
                    it.copy(
                        trackingDayId = selectedDay.uuid,
                        date = selectedDay.date.formatTimestampToString(),
                        foodItems = selectedDay.foodItems
                    )
                }
            }
        }
    }

    fun onDeleteFoodItem(foodItemId: String, trackingDayId: String) {
        sharedNutritionStateRepository.updateDialog(
            DialogInfo(
                R.string.delete_fooditem,
                R.string.delete_fooditem_confirmation,
                R.string.delete,
                R.string.cancel,
                { onDeleteConfirmation(foodItemId, trackingDayId) },
                this::hideDeleteDialog
            )
        )
    }

    fun onEditFoodItem(foodItem: FoodItem, trackingDayId: String) {
        viewModelScope.launch {
            nutritionRepository.editFoodItemFromTrackingDay(foodItem, trackingDayId)
        }

        _uiState.update { state ->
            state.copy(foodItems = uiState.value.foodItems.map {
                if (it.uuid == foodItem.uuid) foodItem else it
            }, selectedFoodItem = null)
        }
    }

    fun onShowEditDialogChange(selectedFoodItem: FoodItem? ) {
        _uiState.update { it.copy(selectedFoodItem = selectedFoodItem)}
    }

    private fun onDeleteConfirmation(foodItemId: String, trackingDayId: String) {
        viewModelScope.launch {

            _uiState.update { state -> state.copy(foodItems = uiState.value.foodItems.filterNot { food -> food.uuid == foodItemId }) }
            nutritionRepository.deleteFoodItemFromTrackingDay(foodItemId, trackingDayId)
            hideDeleteDialog()

        }
    }

    private fun hideDeleteDialog() {
        sharedNutritionStateRepository.updateDialog(null)
    }

    fun onDismiss() {
        _uiState.update { HistoryDetailsUiState() }
        sharedNutritionStateRepository.dismissBottomSheet()
        sharedNutritionStateRepository.updateSelectedTrackingDay(null)
    }
}