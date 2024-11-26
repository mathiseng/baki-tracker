package com.example.baki_tracker.navigation.destinations

/**
 * This interface serves as a general interface for all Tab-Destinations e.g. NutritionTabDestinations and WorkoutTabDestinations to make components easier reusable
 * and reduce boilerplate code
 */
interface TabDestination {
    val label: Int
    val route: Any
}