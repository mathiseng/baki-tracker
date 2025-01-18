package com.example.baki_tracker.model.workout

import java.util.UUID

data class TrackedWorkoutExercise(
    var uuid: String = UUID.randomUUID().toString(),
    var exerciseId: String = "",
    var name: String = "",
    var sets: List<WorkoutSet> = emptyList()
)
