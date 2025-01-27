package com.example.baki_tracker.workout.options

import com.example.baki_tracker.model.workout.PlannedWorkout
import com.example.baki_tracker.model.workout.Workout
import com.example.baki_tracker.model.workout.WorkoutTrackingSession

data class OptionsUiState(
    val selectedWorkout: Workout?,
    val selectedWorkoutTrackingSession: WorkoutTrackingSession?,
    val selectedPlannedWorkout: PlannedWorkout?,
) {
    companion object {
        fun initialUiState() = OptionsUiState(null, null, null)
    }

}
