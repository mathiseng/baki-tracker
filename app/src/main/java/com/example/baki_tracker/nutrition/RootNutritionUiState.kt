package com.example.baki_tracker.nutrition

import com.example.baki_tracker.workout.components.DialogInfo

data class RootNutritionUiState(
    val selectedBottomSheet: NutritionBottomSheet, val dialogInfo: DialogInfo?
) {

    companion object {
        fun initialUiState(): RootNutritionUiState {
            return RootNutritionUiState(NutritionBottomSheet.NONE,null)
        }
    }
}
