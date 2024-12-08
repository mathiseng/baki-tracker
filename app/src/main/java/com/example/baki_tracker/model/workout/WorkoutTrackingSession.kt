package com.example.baki_tracker.model.workout

import com.google.firebase.Timestamp
import java.util.UUID

data class WorkoutTrackingSession(
    var uuid: String = UUID.randomUUID().toString(),
    var workoutId: String = "",
    val date: Timestamp = Timestamp.now(),
    var trackedExercises: List<TrackedWorkoutExercise> = emptyList()
)
