package com.example.baki_tracker.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.baki_tracker.dependencyInjection.WorkoutDependencyProvider
import kotlinx.serialization.Serializable

/**
 * This NavGraph is used to implement type-safe navigation using the existing screens
 */
@Composable
fun WorkoutNavGraph(
    navController: NavHostController, workoutDependencyProvider: WorkoutDependencyProvider
) {
    NavHost(navController = navController, startDestination = WorkoutScreens.TrackingScreen) {
        composable<WorkoutScreens.TrackingScreen> {
            Text("Tracking")
        }
        composable<WorkoutScreens.WorkoutsScreen> {
            workoutDependencyProvider.workoutsScreen()
        }
        composable<WorkoutScreens.ExercisesScreen> {
            Text("Exercises")
        }
    }
}

sealed class WorkoutScreens {
    @Serializable
    object WorkoutsScreen

    @Serializable
    object TrackingScreen

    @Serializable
    object ExercisesScreen
}