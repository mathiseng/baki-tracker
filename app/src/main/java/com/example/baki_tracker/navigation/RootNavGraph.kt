package com.example.baki_tracker.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.baki_tracker.nutrition.RootNutritionContainer
import com.example.baki_tracker.workout.RootWorkoutContainer
import kotlinx.serialization.Serializable

/**
 * This NavGraph is used to implement type-safe navigation using the existing screens
 */
@Composable
fun RootNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = RootScreens.WorkoutScreen) {
        composable<RootScreens.WorkoutScreen> {
            RootWorkoutContainer()
        }
        composable<RootScreens.NutritionScreen> {
            RootNutritionContainer()
        }
        composable<RootScreens.ProfileScreen> {
            Text("Profile")
        }
    }
}

sealed class RootScreens {
    @Serializable
    object WorkoutScreen

    @Serializable
    object NutritionScreen

    @Serializable
    object ProfileScreen
}

