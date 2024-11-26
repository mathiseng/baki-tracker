package com.example.baki_tracker.navigation.destinations

import androidx.annotation.StringRes
import com.example.baki_tracker.R
import com.example.baki_tracker.navigation.NutritionScreens

/**
 * The destinations are used to read out information within the UI components and to build up the navigation correctly
 */
enum class NutritionTabDestinations(
    @StringRes override val label: Int, override val route: Any
) : TabDestination {
    HISTORY(R.string.history, NutritionScreens.HistoryScreen),
    TRACKING(R.string.tracking, NutritionScreens.TrackingScreen),
    GOALS(R.string.goals, NutritionScreens.GoalsScreen),
}