package com.example.baki_tracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.baki_tracker.dependencyInjection.RootDependencyProvider
import kotlinx.serialization.Serializable

/**
 * This NavGraph is used to implement type-safe navigation using the existing screens
 */
@Composable
fun RootNavGraph(
    navController: NavHostController,
    rootDependencyProvider: RootDependencyProvider
) {
    NavHost(navController = navController, startDestination = RootScreens.WorkoutScreen) {
        composable<RootScreens.WorkoutScreen> {
            rootDependencyProvider.rootWorkoutContainer()
        }
        composable<RootScreens.NutritionScreen> {
            rootDependencyProvider.rootNutritionContainer()
        }
        composable<RootScreens.ProfileScreen> {
            rootDependencyProvider.rootProfileContainer()

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

