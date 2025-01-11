package com.example.baki_tracker.workout.workouts.manage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
    val sheetState = rememberModalBottomSheetState(
        confirmValueChange = {
            SheetValue.Hidden != it
        }, skipPartiallyExpanded = true
    )


    ModalBottomSheet(
        modifier = Modifier.statusBarsPadding(), onDismissRequest = {

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
            onDeleteSetFromExercise = viewmodel::deleteSetFromExercise,
            onSaveWorkout = viewmodel::onSaveWorkout,
            onDismiss = viewmodel::onDismiss,
            onWorkoutNameChange = viewmodel::onWorkoutNameChange,
            onWorkoutTypeChange = viewmodel::onWorkoutTypeChange
        )

        //Because we disabled onDismiss to not accidently close the bottomSheet we need to handle the logic when the back button is pressed
        BackHandler {
            viewmodel.onDismiss()
        }
    }
}