package com.example.baki_tracker.workout.workouts.options

import com.example.baki_tracker.model.workout.Workout

data class OptionsUiState(
    val showDeleteDialog: Boolean,
    val selectedWorkout: Workout?
) {
    companion object {

        fun initialUiState() = OptionsUiState(false, null)
    }

}
