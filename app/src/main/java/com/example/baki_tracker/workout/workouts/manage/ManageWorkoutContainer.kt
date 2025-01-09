package com.example.baki_tracker.workout.workouts.manage

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.baki_tracker.dependencyInjection.viewModel
import me.tatarka.inject.annotations.Inject

typealias ManageWorkoutContainer = @Composable () -> Unit

@OptIn(ExperimentalMaterial3Api::class)
@Inject
@Composable
fun ManageWorkoutContainer(manageWorkoutViewModel: () -> ManageWorkoutViewModel) {
    val viewmodel = viewModel { manageWorkoutViewModel() }
    val uiState by viewmodel.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }


    ModalBottomSheet(
        onDismissRequest = {
            showBottomSheet = false
        }, sheetState = sheetState
    ) {
        // Sheet content
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
}