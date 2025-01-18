package com.example.baki_tracker.workout.workouts.manage

import com.example.baki_tracker.model.workout.Workout
import com.example.baki_tracker.model.workout.WorkoutSet
import com.example.baki_tracker.model.workout.WorkoutType
import java.util.UUID

/**
 * The uiState is the value which gets managed through the ManageWorkoutViewModel
 * the values of the ManageWorkoutUiState are used in the Compose-Ui components
 */
data class ManageWorkoutUiState(
    val workout: Workout?,
    val workoutName: String,
    val workoutType: WorkoutType?,
    val exercises: List<WorkoutExerciseUi>,
) {

    /**
     * this method is provided to create an initial uiState or set uiState back to default much easier
     */
    companion object {
        fun initialUiState(): ManageWorkoutUiState =
            ManageWorkoutUiState(null,"", null, emptyList())
    }
}

enum class ManageWorkoutMode {
    CREATE, EDIT, TRACK, TRACK_FREE, EDIT_TRACK
}

//To handle the different data classes create a unified one for the UI
data class WorkoutExerciseUi(var uuid: String = UUID.randomUUID().toString(),
                             var name: String = "",
                             var sets: List<WorkoutSet> = emptyList(),)
