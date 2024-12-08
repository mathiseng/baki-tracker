package com.example.baki_tracker.model

data class User(
    val uuid: String = "",
    val email: String = "",
    val age: Int = 0,
    val weight: Double = 0.0,
    val height: Double = 0.0,
    val gender: String = "",
    val activityLevel: String = ""
)
