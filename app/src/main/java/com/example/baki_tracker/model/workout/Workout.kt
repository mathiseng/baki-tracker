package com.example.baki_tracker.model.workout

import java.util.UUID

data class Workout(
    var uuid: String = UUID.randomUUID().toString(),
    var name: String = "",
    var exercises: List<WorkoutExercise> = emptyList(),
)