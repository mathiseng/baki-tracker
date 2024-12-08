package com.example.baki_tracker.model.workout

data class WorkoutExercise(var uuid: String, var name: String, var sets: List<WorkoutSet>)