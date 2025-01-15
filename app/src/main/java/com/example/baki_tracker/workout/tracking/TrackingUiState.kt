package com.example.baki_tracker.workout.tracking

import com.example.baki_tracker.model.workout.Workout
import com.example.baki_tracker.model.workout.WorkoutTrackingSession

data class TrackingUiState(
    val sessionList: List<WorkoutTrackingSession> = emptyList(),
    val workoutList: List<Workout> = emptyList(),
    val showTrackingDialog: Boolean = false
)

enum class TrackingMode {
    FREE, PREDEFINED
}