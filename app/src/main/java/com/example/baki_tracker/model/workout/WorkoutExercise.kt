package com.example.baki_tracker.model.workout

import java.util.UUID

data class WorkoutExercise(
    var uuid: String = UUID.randomUUID().toString(),
    var name: String = "",
    var sets: List<WorkoutSet> = emptyList(),
    var restBetweenSets: Double = 30.0, //Default: 30 seconds
)