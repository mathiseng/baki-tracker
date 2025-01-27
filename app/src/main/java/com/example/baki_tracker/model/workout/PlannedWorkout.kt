package com.example.baki_tracker.model.workout

data class PlannedWorkout(
    val eventId: String, // Google Calendar Event ID
    val workoutId: String,
    val title: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val calendarId: String
)