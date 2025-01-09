package com.example.baki_tracker.model.workout

import java.util.UUID

data class Workout(
    var uuid: String = UUID.randomUUID().toString(),
    var name: String = "",
    var type: WorkoutType? = WorkoutType.Gym,
    var exercises: List<WorkoutExercise> = emptyList(),
)

//later could extend to sealed class to enhance customizability
sealed class WorkoutType(val value: String) {
    object Gym : WorkoutType("Gym")
    object Bodyweight : WorkoutType("Bodyweight")
    object Cardio : WorkoutType("Cardio")
    object Hiit : WorkoutType("HIIT")
    object Yoga : WorkoutType("Yoga")
    object Pilates : WorkoutType("Pilates")
    object Crossfit : WorkoutType("Crossfit")
    object Running : WorkoutType("Running")
    object Cycling : WorkoutType("Cycling")
    object Swimming : WorkoutType("Swimming")

    data class Custom(val name: String) :
        WorkoutType(name) // Custom types use their `name` as the value
}