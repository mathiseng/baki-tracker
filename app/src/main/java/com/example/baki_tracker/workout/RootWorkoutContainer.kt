package com.example.baki_tracker.workout

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import com.example.baki_tracker.dependencyInjection.WorkoutDependencyProvider
import com.example.baki_tracker.dependencyInjection.viewModel
import com.example.baki_tracker.navigation.WorkoutNavGraph
import com.example.baki_tracker.navigation.destinations.WorkoutTabDestinations
import com.example.baki_tracker.workout.components.CustomConfirmationDialog
import com.example.baki_tracker.workout.workouts.manage.ManageWorkoutMode
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
            WorkoutBottomSheet.ADD -> workoutDependencyProvider.manageWorkoutContainer(ManageWorkoutMode.CREATE)
            WorkoutBottomSheet.ADD_PLANNED ->workoutDependencyProvider.planBottomSheet(false)
            WorkoutBottomSheet.EDIT -> workoutDependencyProvider.manageWorkoutContainer(ManageWorkoutMode.EDIT)
            WorkoutBottomSheet.EDIT_TRACK -> workoutDependencyProvider.manageWorkoutContainer(ManageWorkoutMode.EDIT_TRACK)
            WorkoutBottomSheet.EDIT_PLANNED -> workoutDependencyProvider.planBottomSheet(true)
            WorkoutBottomSheet.TRACK -> workoutDependencyProvider.manageWorkoutContainer(ManageWorkoutMode.TRACK)
            WorkoutBottomSheet.TRACK_FREE -> workoutDependencyProvider.manageWorkoutContainer(ManageWorkoutMode.TRACK_FREE)
            WorkoutBottomSheet.OPTIONS -> workoutDependencyProvider.optionsContainer()
            else -> {}
        }
    }
    uiState.dialogInfo?.let {
        CustomConfirmationDialog(
            stringResource(it.title),
            stringResource(it.description),
            stringResource(it.confirmButtonLabel),
            stringResource(it.dismissButtonLabel),
            it.onConfirm,
            it.onDismiss
        )
    }
}