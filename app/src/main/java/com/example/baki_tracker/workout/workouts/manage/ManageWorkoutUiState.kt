package com.example.baki_tracker.workout.workouts.manage

import com.example.baki_tracker.model.workout.WorkoutExercise
import com.example.baki_tracker.model.workout.WorkoutType

/**
 * The uiState is the value which gets managed through the ManageWorkoutViewModel
 * the values of the ManageWorkoutUiState are used in the Compose-Ui components
 */
data class ManageWorkoutUiState(
    val workoutName: String,
    val workoutType: WorkoutType?,
    val exercises: List<WorkoutExercise>
) {

    /**
     * this method is provided to create an initial uiState or set uiState back to default much easier
     */
    companion object {
        fun initialUiState(): ManageWorkoutUiState =
            ManageWorkoutUiState("", null, emptyList())
    }
}