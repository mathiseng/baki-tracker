package com.example.baki_tracker.workout.workouts.options

import com.example.baki_tracker.model.workout.Workout

data class OptionsUiState(
    val selectedWorkout: Workout?
) {
    companion object {
        fun initialUiState() = OptionsUiState( null)
    }

}
