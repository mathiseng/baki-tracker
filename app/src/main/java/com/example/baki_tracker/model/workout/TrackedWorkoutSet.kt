package com.example.baki_tracker.model.workout

import java.util.UUID

data class TrackedWorkoutSet(
    var uuid: String = UUID.randomUUID().toString(),
    val reps: Int = 0,
    val weight: Double = 0.0
)
