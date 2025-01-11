package com.example.baki_tracker.workout.workouts.options

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baki_tracker.repository.IWorkoutDatabaseRepository
import com.example.baki_tracker.workout.ISharedWorkoutStateRepository
import com.example.baki_tracker.workout.WorkoutBottomSheet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class OptionsViewModel(
    private val sharedWorkoutStateRepository: ISharedWorkoutStateRepository,
    val workoutDatabaseRepository: IWorkoutDatabaseRepository
) :
    ViewModel() {

    private val _uiState = MutableStateFlow(OptionsUiState.initialUiState())
    val uiState: StateFlow<OptionsUiState> = _uiState

    init {
        viewModelScope.launch {
            sharedWorkoutStateRepository.selectedWorkout.collect { workout ->
                _uiState.update { it.copy(selectedWorkout = workout) }
            }
        }
    }

    fun onEditClick() {
        sharedWorkoutStateRepository.updateSelectedBottomSheet(WorkoutBottomSheet.EDIT)
    }

    fun onDeleteClick() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun onDeleteConfirmation(uuid: String) {
        viewModelScope.launch {
            try {
                workoutDatabaseRepository.deleteWorkout(uuid)
                onDismiss()
            } catch (_: Exception) {
            }
        }
    }

    fun hideDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun onDismiss() {
        _uiState.update { OptionsUiState.initialUiState() }
        sharedWorkoutStateRepository.dismissBottomSheet()
        sharedWorkoutStateRepository.updateSelectedWorkout(null)
    }
}