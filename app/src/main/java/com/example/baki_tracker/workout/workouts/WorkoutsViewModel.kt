package com.example.baki_tracker.workout.workouts

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
class WorkoutsViewModel(
    workoutDatabaseRepository: IWorkoutDatabaseRepository,
    val sharedWorkoutStateRepository: ISharedWorkoutStateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutsUiState(emptyList()))
    val uiState: StateFlow<WorkoutsUiState> = _uiState

    init {
        viewModelScope.launch {
            workoutDatabaseRepository.getWorkouts()
        }
        viewModelScope.launch {
            workoutDatabaseRepository.workouts.collect { workouts ->
                _uiState.update { it.copy(workoutList = workouts) }
            }
        }
    }

    fun onAddWorkout() {
        sharedWorkoutStateRepository.updateSelectedBottomSheet(WorkoutBottomSheet.ADD)
    }

    fun onOptionsSelected(workout: Workout) {
        sharedWorkoutStateRepository.updateSelectedBottomSheet(WorkoutBottomSheet.OPTIONS)
        sharedWorkoutStateRepository.updateSelectedWorkout(workout)
    }
}