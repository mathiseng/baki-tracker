package com.example.baki_tracker.navigation.destinations

import androidx.annotation.StringRes
import com.example.baki_tracker.R
import com.example.baki_tracker.navigation.WorkoutScreens

/**
 * The destinations are used to read out information within the UI components and to build up the navigation correctly
 */
enum class WorkoutTabDestinations(
    @StringRes override val label: Int, override val route: Any
) : TabDestination {
    TRACKING(R.string.tracking, WorkoutScreens.TrackingScreen),
    WORKOUTS(R.string.workouts, WorkoutScreens.WorkoutsScreen),
    PLANNING(R.string.schedule, WorkoutScreens.ScheduleScreen),
}