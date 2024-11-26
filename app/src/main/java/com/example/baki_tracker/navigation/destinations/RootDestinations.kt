package com.example.baki_tracker.navigation.destinations

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.baki_tracker.R
import com.example.baki_tracker.navigation.RootScreens

/**
 * The destinations are used to read out information within the UI components and to build up the navigation correctly
 */
enum class RootDestinations(
    @StringRes val label: Int,
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    val route: Any
) {
    WORKOUT(
        R.string.workout,
        R.drawable.ic_workout_selected,
        R.drawable.ic_workout_unselected,
        RootScreens.WorkoutScreen
    ),
    NUTRITION(
        R.string.nutrition,
        R.drawable.ic_grocery,
        R.drawable.ic_nutrition_unselected,
        RootScreens.NutritionScreen
    ),
    PROFILE(
        R.string.profile,
        R.drawable.ic_profile_selected,
        R.drawable.ic_profile_unselected,
        RootScreens.ProfileScreen
    ),
}