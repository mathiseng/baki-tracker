package com.example.baki_tracker.workout.tracking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.baki_tracker.R
import com.example.baki_tracker.model.dummydata.DummyData
import com.example.baki_tracker.model.workout.Workout
import com.example.components.scrollbar

@Composable
fun SelectionDialog(
    uiState: TrackingUiState,
    dialogState: DialogState = rememberDialogState(),
    onDismiss: () -> Unit,
    onStartFreeWorkout: () -> Unit,
    onStartPredefinedWorkout: (Workout) -> Unit
) {
    AlertDialog(modifier = Modifier.fillMaxHeight(0.47f),
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(enabled = !(dialogState.showWorkoutList && dialogState.selectedWorkout == null),
                onClick = {
                    when {
                        dialogState.selectedMode == TrackingMode.FREE -> onStartFreeWorkout()
                        dialogState.showWorkoutList && dialogState.selectedWorkout != null -> {
                            onStartPredefinedWorkout(dialogState.selectedWorkout)
                        }

                        else -> dialogState.toggleWorkoutList()
                    }
                }) {
                val text =
                    if (dialogState.selectedMode == TrackingMode.FREE || dialogState.showWorkoutList) stringResource(
                        R.string.start
                    )
                    else stringResource(R.string.next)
                Text(text)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                if (dialogState.showWorkoutList) dialogState.toggleWorkoutList()
                else onDismiss()
            }) {
                val text = if (dialogState.showWorkoutList) stringResource(
                    R.string.back
                ) else stringResource(
                    R.string.cancel
                )
                Text(text)
            }
        },
        title = { Text(text = stringResource(R.string.select_tracking_mode)) },
        text = {
            if (!dialogState.showWorkoutList) {
                WorkoutModeSelection(
                    selectedMode = dialogState.selectedMode,
                    onSelectMode = dialogState.setSelectedMode
                )
            } else {
                PredefinedWorkoutList(
                    workoutList = uiState.workoutList,
                    selectedWorkout = dialogState.selectedWorkout,
                    onChangeSelection = dialogState.setSelectedWorkout
                )
            }
        })
}

@Composable
private fun WorkoutModeSelection(
    selectedMode: TrackingMode, onSelectMode: (TrackingMode) -> Unit
) {
    val selectedBorder = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    val unselectedBorder = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            onClick = { onSelectMode(TrackingMode.FREE) },
            shape = RoundedCornerShape(16),
            border = if (selectedMode == TrackingMode.FREE) selectedBorder else unselectedBorder,
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(Modifier.padding(8.dp)) {
                Text(fontWeight = FontWeight.W600, text = stringResource(R.string.free_workout))
                Text(text = stringResource(R.string.free_workout_description))
            }
        }
        Spacer(Modifier.height(8.dp))
        Surface(
            onClick = { onSelectMode(TrackingMode.PREDEFINED) },
            shape = RoundedCornerShape(16),
            border = if (selectedMode == TrackingMode.PREDEFINED) selectedBorder else unselectedBorder,
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(Modifier.padding(8.dp)) {
                Text(
                    fontWeight = FontWeight.W600, text = stringResource(R.string.predefined_workout)
                )
                Text(text = stringResource(R.string.predefined_workout_description))
            }
        }
    }
}

@Composable
private fun PredefinedWorkoutList(
    workoutList: List<Workout>, selectedWorkout: Workout?, onChangeSelection: (Workout) -> Unit
) {
    // State für das Scrollen
    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Die Workout-Liste
        LazyColumn(
            state = listState, // Scroll-State der LazyColumn
            modifier = Modifier
                .fillMaxSize()
                .scrollbar(listState, horizontal = false, alignEnd = false)
        ) {
            itemsIndexed(workoutList) { index, workout ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { onChangeSelection(workout) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        workout.name, modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        if (selectedWorkout == workout) {
                            Icon(Icons.Default.Check, contentDescription = "Selected")
                        }
                    }
                }
                HorizontalDivider()
            }
        }
    }
}


//Because this Dialog is quite small it would be an overhead for now to create an extra viewmodel for this component. To recude boilerplate code i kept it in this file as inline State
@Composable
private fun rememberDialogState(): DialogState {
    var selectedMode by remember { mutableStateOf(TrackingMode.FREE) }
    var showWorkoutList by remember { mutableStateOf(false) }
    var selectedWorkout: Workout? by remember { mutableStateOf(null) }

    return DialogState(selectedMode = selectedMode,
        setSelectedMode = { selectedMode = it },
        showWorkoutList = showWorkoutList,
        toggleWorkoutList = { showWorkoutList = !showWorkoutList },
        selectedWorkout = selectedWorkout,
        setSelectedWorkout = { selectedWorkout = it })
}

data class DialogState(
    val selectedMode: TrackingMode,
    val setSelectedMode: (TrackingMode) -> Unit,
    val showWorkoutList: Boolean,
    val toggleWorkoutList: () -> Unit,
    val selectedWorkout: Workout?,
    val setSelectedWorkout: (Workout?) -> Unit
)

@Preview(showBackground = true)
@Composable
fun PredefinedWorkoutListPreview() {
    PredefinedWorkoutList(DummyData.workouts, null, {})

}