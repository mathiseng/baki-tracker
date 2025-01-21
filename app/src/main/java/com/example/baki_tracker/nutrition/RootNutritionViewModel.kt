package com.example.baki_tracker.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject

@Inject
class RootNutritionViewModel(sharedNutritionStateRepository: ISharedNutritionStateRepository) :
    ViewModel() {

    val uiState: StateFlow<RootNutritionUiState> = combine(
        sharedNutritionStateRepository.selectedBottomSheet,
        sharedNutritionStateRepository.dialog
    ) { bottomSheet, dialog ->

        RootNutritionUiState( bottomSheet, dialog)

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = RootNutritionUiState.initialUiState(),
    )
}