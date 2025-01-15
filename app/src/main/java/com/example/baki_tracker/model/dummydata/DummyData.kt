package com.example.baki_tracker.model.dummydata

import com.example.baki_tracker.model.workout.Workout
import com.example.baki_tracker.model.workout.WorkoutExercise
import com.example.baki_tracker.model.workout.WorkoutSet
import com.example.baki_tracker.model.workout.WorkoutTrackingSession
import com.example.baki_tracker.model.workout.WorkoutType

object DummyData {
    val workoutTrackingSession = WorkoutTrackingSession()

    val workout = Workout(
        uuid = "1",
        name = "Full Body Workout",
        type = WorkoutType.Bodyweight.toMap(),
        exercises = listOf(
            WorkoutExercise(
                uuid = "1", name = "Bench Press", sets = listOf(
                    WorkoutSet(uuid = "1", reps = 10, weight = 50.0),
                    WorkoutSet(uuid = "2", reps = 8, weight = 55.0)
                )
            ), WorkoutExercise(
                uuid = "2", name = "Squats", sets = listOf(
                    WorkoutSet(uuid = "3", reps = 12, weight = 60.0),
                    WorkoutSet(uuid = "4", reps = 10, weight = 65.0)
                )
            )
        )
    )
    val workouts = listOf(workout, workout.copy(uuid = "2", name = "Push Workout"))
}