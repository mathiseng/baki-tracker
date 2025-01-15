package com.example.baki_tracker.workout.tracking

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baki_tracker.model.workout.Workout
import com.example.baki_tracker.repository.IWorkoutDatabaseRepository
import com.example.baki_tracker.workout.ISharedWorkoutStateRepository
import com.example.baki_tracker.workout.WorkoutBottomSheet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class TrackingViewModel(
    workoutDatabaseRepository: IWorkoutDatabaseRepository,
    val sharedWorkoutStateRepository: ISharedWorkoutStateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrackingUiState(emptyList()))
    val uiState: StateFlow<TrackingUiState> = _uiState

    init {
        viewModelScope.launch {
            workoutDatabaseRepository.getWorkoutTrackingSessions()
        }
        viewModelScope.launch {
            workoutDatabaseRepository.workoutTrackingSessions.collect { sessions ->
                _uiState.update { it.copy(sessionList = sessions) }
            }
        }

        viewModelScope.launch {
            workoutDatabaseRepository.getWorkouts()
        }
        viewModelScope.launch {
            workoutDatabaseRepository.workouts.collect { workouts ->
                _uiState.update { it.copy(workoutList = workouts) }
            }
        }
    }


//    fun onOptionsSelected(workoutTrackingSession: WorkoutTrackingSession) {
//        sharedWorkoutStateRepository.updateSelectedBottomSheet(WorkoutBottomSheet.TRACKING_OPTIONS)
//        // sharedWorkoutStateRepository.updateSelectedWorkout(workoutTrackingSession)
//    }

    fun onTrackWorkout() {
        _uiState.update {
            it.copy(
                showTrackingDialog = true
            )
        }
    }

    fun onStartFreeWorkout() {
        _uiState.update { it.copy(showTrackingDialog = false) }
        sharedWorkoutStateRepository.updateSelectedBottomSheet(WorkoutBottomSheet.TRACK_FREE)
    }

    fun onStartPredefinedWorkout(workout: Workout) {
        _uiState.update { it.copy(showTrackingDialog = false) }
        sharedWorkoutStateRepository.updateSelectedBottomSheet(WorkoutBottomSheet.TRACK)
        sharedWorkoutStateRepository.updateSelectedWorkout(workout)
    }


    fun onDismissDialog() {
        _uiState.update {
            it.copy(
                showTrackingDialog = false
            )
        }
    }
}