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

        when {
            uiState.selectedWorkout != null -> {
                val workout = uiState.selectedWorkout
                OptionsScreen(
                    elementName = stringResource(R.string.workout),
                    name = workout!!.name,
                    description = "${workout.exercises.size} ${stringResource(R.string.exercises)}",
                    workoutType = workout.workoutType,
                    onEditClick = viewModel::onEditClick,
                    onDeleteClick = { viewModel.onDeleteClick(workout.uuid) },
                )
            }

            uiState.selectedWorkoutTrackingSession != null -> {
                val session = uiState.selectedWorkoutTrackingSession
                OptionsScreen(
                    elementName = stringResource(R.string.workout_tracking),
                    name = session!!.name,
                    description = "${session.trackedExercises.size} ${stringResource(R.string.exercises)}",
                    onEditClick = viewModel::onEditClick,
                    onDeleteClick = { viewModel.onDeleteClick(session.uuid) },
                    date = session.date.formatTimestampToString()
                )
            }

            uiState.selectedPlannedWorkout != null -> {
                val plannedWorkout = uiState.selectedPlannedWorkout
                OptionsScreen(
                    elementName = stringResource(R.string.workout_planned),
                    name = plannedWorkout!!.title,
                    description = plannedWorkout.description,
                    onEditClick = viewModel::onEditClick,
                    onDeleteClick = { viewModel.onDeleteClick(plannedWorkout.eventId) },
                    date = plannedWorkout.date
                )

            }
        }
    }
}


