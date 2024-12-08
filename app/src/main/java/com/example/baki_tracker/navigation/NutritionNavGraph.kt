package com.example.baki_tracker.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

/**
 * This NavGraph is used to implement type-safe navigation using the existing screens
 */
@Composable
fun NutritionNavGraph(navController: NavHostController, trackingScreen: @Composable () -> Unit) {
    NavHost(navController = navController, startDestination = NutritionScreens.HistoryScreen) {
        composable<NutritionScreens.HistoryScreen> {
            Text("History")
        }
      composable<NutritionScreens.TrackingScreen> {
          trackingScreen()
        }
        composable<NutritionScreens.GoalsScreen> {
            Text("Goals")
        }
    }
}


sealed class NutritionScreens {
    @Serializable
    object HistoryScreen

    @Serializable
    object TrackingScreen

    @Serializable
    object GoalsScreen
}