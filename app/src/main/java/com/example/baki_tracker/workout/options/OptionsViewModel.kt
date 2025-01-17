package com.example.baki_tracker.workout.options

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baki_tracker.R
import com.example.baki_tracker.repository.IWorkoutDatabaseRepository
import com.example.baki_tracker.workout.ISharedWorkoutStateRepository
import com.example.baki_tracker.workout.WorkoutBottomSheet
import com.example.baki_tracker.workout.components.DialogInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class OptionsViewModel(
    private val sharedWorkoutStateRepository: ISharedWorkoutStateRepository,
    val workoutDatabaseRepository: IWorkoutDatabaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OptionsUiState.initialUiState())
    val uiState: StateFlow<OptionsUiState> = _uiState

    init {
        viewModelScope.launch {
            sharedWorkoutStateRepository.selectedWorkout.collect { workout ->
                _uiState.update { it.copy(selectedWorkout = workout) }
            }
        }

        viewModelScope.launch {
            sharedWorkoutStateRepository.selectedWorkoutTrackingSession.collect { session ->
                _uiState.update { it.copy(selectedWorkoutTrackingSession = session) }
            }
        }
    }

    fun onEditClick() {
        val uistate = _uiState.value
        when {
            uistate.selectedWorkout != null -> sharedWorkoutStateRepository.updateSelectedBottomSheet(
                WorkoutBottomSheet.EDIT
            )

            //EDIT_SESSION has to be implemented
            uistate.selectedWorkoutTrackingSession != null -> sharedWorkoutStateRepository.updateSelectedBottomSheet(
                WorkoutBottomSheet.EDIT
            )

        }
    }

    fun onDeleteClick(uuid: String) {
        val uistate = _uiState.value
        when {
            uistate.selectedWorkout != null -> sharedWorkoutStateRepository.updateDialog(
                DialogInfo(
                    R.string.delete_workout,
                    R.string.delete_workout_confirmation,
                    R.string.delete,
                    R.string.cancel,
                    { onDeleteConfirmation(uuid) },
                    this::hideDeleteDialog
                )
            )

            uistate.selectedWorkoutTrackingSession != null -> sharedWorkoutStateRepository.updateDialog(
                DialogInfo(
                    R.string.delete_workout_session,
                    R.string.delete_workout_session_confirmation,
                    R.string.delete,
                    R.string.cancel,
                    { onDeleteConfirmation(uuid) },
                    this::hideDeleteDialog
                )
            )
        }
    }

    private fun onDeleteConfirmation(uuid: String) {
        val uistate = _uiState.value
        viewModelScope.launch {
            try {
                when {
                    uistate.selectedWorkout != null -> workoutDatabaseRepository.deleteWorkout(uuid)
                    uistate.selectedWorkoutTrackingSession != null -> workoutDatabaseRepository.deleteWorkoutTrackingSession(
                        uuid
                    )
                }
                onDismiss()
            } catch (_: Exception) {
            }
        }
    }

    private fun hideDeleteDialog() {
        sharedWorkoutStateRepository.updateDialog(null)
    }

    fun onDismiss() {
        _uiState.update { OptionsUiState.initialUiState() }
        sharedWorkoutStateRepository.dismissBottomSheet()
        sharedWorkoutStateRepository.updateSelectedWorkout(null)
        sharedWorkoutStateRepository.updateSelectedWorkoutTrackingSession(null)
        sharedWorkoutStateRepository.updateDialog(null)
    }
}