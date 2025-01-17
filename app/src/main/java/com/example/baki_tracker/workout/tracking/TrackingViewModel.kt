package com.example.baki_tracker.workout.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baki_tracker.model.workout.Workout
import com.example.baki_tracker.model.workout.WorkoutTrackingSession
import com.example.baki_tracker.repository.IWorkoutDatabaseRepository
import com.example.baki_tracker.utils.formatTimestampToString
import com.example.baki_tracker.utils.getCurrentDateString
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

    private val _uiState = MutableStateFlow(TrackingUiState())
    val uiState: StateFlow<TrackingUiState> = _uiState

    val currentDateString = getCurrentDateString()

    init {
        viewModelScope.launch {
            workoutDatabaseRepository.getWorkoutTrackingSessions()
        }
        viewModelScope.launch {
            workoutDatabaseRepository.workoutTrackingSessions.collect { sessions ->
                _uiState.update { it.copy(sessionMap = groupSessionsByDate(sessions)) }
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


    fun onOptionsSelected(workoutTrackingSession: WorkoutTrackingSession) {
        sharedWorkoutStateRepository.updateSelectedWorkoutTrackingSession(workoutTrackingSession)
        sharedWorkoutStateRepository.updateSelectedBottomSheet(WorkoutBottomSheet.OPTIONS)
    }

    private fun groupSessionsByDate(items: List<WorkoutTrackingSession>): Map<String, List<WorkoutTrackingSession>> {
        return items.groupBy { it.date.formatTimestampToString() }
            .toSortedMap(compareByDescending { it })
    }

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

    fun onSelectedSessionChanged(workoutTrackingSession: WorkoutTrackingSession?) {
        _uiState.update { it.copy(selectedSession = workoutTrackingSession) }
    }

    fun onDismissDialog() {
        _uiState.update {
            it.copy(
                showTrackingDialog = false
            )
        }
    }
}