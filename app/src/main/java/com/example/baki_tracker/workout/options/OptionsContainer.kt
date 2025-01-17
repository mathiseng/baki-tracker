package com.example.baki_tracker.workout.options

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.baki_tracker.R
import com.example.baki_tracker.dependencyInjection.viewModel
import com.example.baki_tracker.utils.formatTimestampToString
import me.tatarka.inject.annotations.Inject

typealias OptionsContainer = @Composable () -> Unit


@OptIn(ExperimentalMaterial3Api::class)
@Inject
@Composable
fun OptionsContainer(optionsViewModel: () -> OptionsViewModel) {
    val viewModel = viewModel { optionsViewModel() }
    val uiState by viewModel.uiState.collectAsState()

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        modifier = Modifier.statusBarsPadding(),
        onDismissRequest = { viewModel.onDismiss() },
        sheetState = sheetState
    ) {

        if (uiState.selectedWorkout != null) {
            val workout = uiState.selectedWorkout
            OptionsScreen(
                elementName = stringResource(R.string.workout),
                name = workout!!.name,
                exerciseNumber = workout.exercises.size,
                workoutType = workout.workoutType,
                onEditClick = viewModel::onEditClick,
                onDeleteClick = { viewModel.onDeleteClick(workout.uuid) },
            )
        } else if (uiState.selectedWorkoutTrackingSession != null) {
            val session = uiState.selectedWorkoutTrackingSession
            OptionsScreen(
                elementName = stringResource(R.string.workout_tracking),
                name = session!!.name,
                exerciseNumber = session.trackedExercises.size,
                onEditClick = viewModel::onEditClick,
                onDeleteClick = { viewModel.onDeleteClick(session.uuid) },
                date = session.date.formatTimestampToString()
            )
        }
    }
}


