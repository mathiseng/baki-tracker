package com.example.baki_tracker.workout.manage

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tatarka.inject.annotations.Inject

/**
 * The ManageWorkoutViewModel should manage events from the UI (e.g. clicks)
 * and should update the uiState accordingly. It should provide the actual uiState as a stateFlow to the UI-components
 */
@Inject
class ManageWorkoutViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ManageWorkoutUiState.initialUiState())
    val uiState = _uiState.asStateFlow()

}