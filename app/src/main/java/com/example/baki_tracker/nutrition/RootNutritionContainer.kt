package com.example.baki_tracker.nutrition

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.baki_tracker.navigation.destinations.NutritionTabDestinations
import com.example.baki_tracker.navigation.NutritionNavGraph
import com.example.components.TabBar


@Composable
fun RootNutritionContainer() {

    val navController = rememberNavController()

    Column {
        TabBar(
            NutritionTabDestinations.entries,
            navController = navController
        )

        NutritionNavGraph(navController)
    }
}