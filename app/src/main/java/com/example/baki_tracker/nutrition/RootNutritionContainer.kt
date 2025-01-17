package com.example.baki_tracker.nutrition

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.baki_tracker.navigation.NutritionNavGraph
import com.example.baki_tracker.navigation.destinations.NutritionTabDestinations
import com.example.baki_tracker.nutrition.history.HistoryScreen
import com.example.baki_tracker.nutrition.tracking.TrackingScreen
import com.example.components.TabBar
import me.tatarka.inject.annotations.Inject

typealias RootNutritionContainer = @Composable () -> Unit


@Inject
@Composable
fun RootNutritionContainer(trackingScreen: TrackingScreen, historyScreen: HistoryScreen) {

    val navController = rememberNavController()

    Column {
        TabBar(
            NutritionTabDestinations.entries,
            navController = navController
        )

        NutritionNavGraph(navController, trackingScreen, historyScreen)
    }
}