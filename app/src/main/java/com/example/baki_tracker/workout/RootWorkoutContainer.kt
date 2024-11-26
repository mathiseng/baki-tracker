package com.example.baki_tracker.workout

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.baki_tracker.navigation.destinations.WorkoutTabDestinations
import com.example.baki_tracker.navigation.WorkoutNavGraph
import com.example.components.TabBar


@Composable
fun RootWorkoutContainer() {

    val navController = rememberNavController()

    Column {
        TabBar(
            WorkoutTabDestinations.entries,
            navController = navController
        )

        WorkoutNavGraph(navController)
    }
}