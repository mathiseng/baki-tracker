package com.example.baki_tracker.workout

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.baki_tracker.dependencyInjection.WorkoutDependencyProvider
import com.example.baki_tracker.dependencyInjection.viewModel
import com.example.baki_tracker.navigation.WorkoutNavGraph
import com.example.baki_tracker.navigation.destinations.WorkoutTabDestinations
import com.example.components.TabBar
import me.tatarka.inject.annotations.Inject

typealias RootWorkoutContainer = @Composable () -> Unit

@Inject
@Composable
fun RootWorkoutContainer(workoutDependencyProvider: WorkoutDependencyProvider) {

    val navController = rememberNavController()

    val viewModel = viewModel { workoutDependencyProvider.rootWorkoutViewModel() }
    val uiState by viewModel.uiState.collectAsState()

    Column {
        TabBar(
            WorkoutTabDestinations.entries, navController = navController
        )

        WorkoutNavGraph(navController, workoutDependencyProvider)
    }

    if (uiState.selectedBottomSheet != WorkoutBottomSheet.NONE) {
        when (uiState.selectedBottomSheet) {
            WorkoutBottomSheet.ADD -> workoutDependencyProvider.manageWorkoutContainer()
            else -> {}
        }
    }
}