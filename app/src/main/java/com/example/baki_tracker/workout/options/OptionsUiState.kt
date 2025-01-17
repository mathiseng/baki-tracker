package com.example.baki_tracker.workout.options

import com.example.baki_tracker.model.workout.Workout
import com.example.baki_tracker.model.workout.WorkoutTrackingSession

data class OptionsUiState(
    val selectedWorkout: Workout?,
    val selectedWorkoutTrackingSession: WorkoutTrackingSession?
) {
    companion object {
        fun initialUiState() = OptionsUiState(null, null)
    }

}
