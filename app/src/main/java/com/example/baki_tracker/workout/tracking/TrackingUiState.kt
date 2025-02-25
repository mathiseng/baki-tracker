package com.example.baki_tracker.workout.tracking

import com.example.baki_tracker.model.workout.Workout
import com.example.baki_tracker.model.workout.WorkoutTrackingSession

data class TrackingUiState(
    val sessionMap: Map<String, List<WorkoutTrackingSession>> = emptyMap(),
    val workoutList: List<Workout> = emptyList(),
    val showTrackingDialog: Boolean = false,
    val selectedSession: WorkoutTrackingSession? = null,
)

enum class TrackingMode {
    FREE, PREDEFINED
}