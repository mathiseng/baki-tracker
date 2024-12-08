package com.example.baki_tracker.workout.manage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.baki_tracker.dependencyInjection.viewModel
import me.tatarka.inject.annotations.Inject

typealias ManageWorkoutContainer = @Composable () -> Unit

@Inject
@Composable
fun ManageWorkoutContainer(manageWorkoutViewModel: () -> ManageWorkoutViewModel) {
    val viewmodel = viewModel { manageWorkoutViewModel() }
    val uiState by viewmodel.uiState.collectAsStateWithLifecycle()

    ManageWorkoutScreen(
        uiState = uiState,
        onExerciseNameChange = viewmodel::onExerciseNameChange,
        onSetChange = viewmodel::onSetChange,
        onAddExercise = viewmodel::addExercise,
        onDeleteExercise = viewmodel::deleteExercise,
        onAddSetToExercise = viewmodel::addSetToExercise,
        onDeleteSetFromExercise = viewmodel::deleteSetFromExercise
    )

}