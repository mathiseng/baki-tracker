package com.example.baki_tracker.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject

@Inject
class RootWorkoutViewModel(sharedWorkoutStateRepository: ISharedWorkoutStateRepository) :
    ViewModel() {

    val uiState: StateFlow<RootWorkoutUiState> = combine(
        sharedWorkoutStateRepository.selectedWorkout,
        sharedWorkoutStateRepository.selectedBottomSheet,
        sharedWorkoutStateRepository.dialog
    ) { workout, bottomSheet, dialog ->

        RootWorkoutUiState(workout, bottomSheet, dialog)

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = RootWorkoutUiState.initialUiState(),
    )
}