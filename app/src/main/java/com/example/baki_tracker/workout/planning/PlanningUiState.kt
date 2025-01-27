package com.example.baki_tracker.workout.planning

import com.example.baki_tracker.model.workout.PlannedWorkout
import com.example.baki_tracker.model.workout.Workout

data class PlanningUiState(
    val isAuthenticated: Boolean = false,
    val plannedMap: Map<String, List<PlannedWorkout>> = emptyMap(),
    val workoutList: List<Workout> = emptyList(),
    val selectedPlannedWorkout: PlannedWorkout? = null,
    val showPlanningBottomSheet: Boolean = false
)
