package com.example.baki_tracker.workout

import com.example.baki_tracker.model.workout.Workout
import com.example.baki_tracker.workout.components.DialogInfo

data class RootWorkoutUiState(
    val selectedWorkout: Workout?, val selectedBottomSheet: WorkoutBottomSheet, val dialogInfo: DialogInfo?
) {

    companion object {
        fun initialUiState(): RootWorkoutUiState {
            return RootWorkoutUiState(null, WorkoutBottomSheet.NONE,null)
        }
    }
}
