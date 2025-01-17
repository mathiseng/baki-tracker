package com.example.baki_tracker.workout.workouts

import com.example.baki_tracker.model.workout.Workout

data class WorkoutsUiState(
    val workoutList: List<Workout> = emptyList(),
    val selectedWorkout: Workout? = null,
)
