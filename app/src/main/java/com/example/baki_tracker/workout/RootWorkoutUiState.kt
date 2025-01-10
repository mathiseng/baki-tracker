package com.example.baki_tracker.workout

import com.example.baki_tracker.model.workout.Workout

data class RootWorkoutUiState(
    val selectedWorkout: Workout?, val selectedBottomSheet: WorkoutBottomSheet
) {

    companion object {
        fun initialUiState(): RootWorkoutUiState {
            return RootWorkoutUiState(null, WorkoutBottomSheet.NONE)
        }
    }
}
