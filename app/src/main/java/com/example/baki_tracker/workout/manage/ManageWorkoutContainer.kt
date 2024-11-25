package com.example.baki_tracker.workout.manage

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.baki_tracker.dependencyInjection.viewModel


@Composable
fun ManageWorkoutContainer(manageWorkoutViewModel: () -> ManageWorkoutViewModel) {
    val viewmodel = viewModel { manageWorkoutViewModel() }
    val uiState = viewmodel.uiState.collectAsStateWithLifecycle()


}