package com.example.baki_tracker.model.workout

import java.util.UUID

data class Workout(
    var uuid: String = UUID.randomUUID().toString(),
    var name: String = "",
    var type: Map<String, String> = WorkoutType.Gym.toMap(),
    var exercises: List<WorkoutExercise> = emptyList(),
    var restBetweenExercises: Double = 60.0, //Default: 60 Seconds
) {
    val workoutType: WorkoutType
        get() = WorkoutType.fromMap(type)
}

sealed class WorkoutType(val value: String) {
    object Gym : WorkoutType("Gym")
    object Bodyweight : WorkoutType("Bodyweight")
    object Cardio : WorkoutType("Cardio")
    object Recovery : WorkoutType("Recovery")
    object Hiit : WorkoutType("HIIT")
    object Yoga : WorkoutType("Yoga")
    object Pilates : WorkoutType("Pilates")
    object Crossfit : WorkoutType("Crossfit")
    object Running : WorkoutType("Running")
    object Cycling : WorkoutType("Cycling")
    object Swimming : WorkoutType("Swimming")

    data class Custom(val name: String) :
        WorkoutType(name) // Custom types use their `name` as the value

    //We need to serialize and deserialize this sealed class to a map because Firestore internally converts this to a Map and to deserialize the data back when fetching we need use this as map
    companion object {
        fun fromMap(typeMap: Map<String, String>?): WorkoutType {
            val value = typeMap?.get("value") ?: return Gym // Default to Gym
            return when (value) {
                Gym.value -> Gym
                Bodyweight.value -> Bodyweight
                Cardio.value -> Cardio
                Recovery.value -> Recovery
                Hiit.value -> Hiit
                Yoga.value -> Yoga
                Pilates.value -> Pilates
                Crossfit.value -> Crossfit
                Running.value -> Running
                Cycling.value -> Cycling
                Swimming.value -> Swimming
                else -> Custom(value) // Handle unknown/custom types
            }
        }

        fun getAllWorkoutTypes(): List<WorkoutType> {
            return WorkoutType::class.sealedSubclasses.filter { item ->
                (item.objectInstance is WorkoutType)
            }.map { it.objectInstance as WorkoutType }

        }
    }

    fun toMap(): Map<String, String> {
        return mapOf("value" to this.value)
    }
}