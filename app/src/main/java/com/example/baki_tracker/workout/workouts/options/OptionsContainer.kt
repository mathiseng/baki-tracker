package com.example.baki_tracker.workout.workouts.options

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.baki_tracker.R
import com.example.baki_tracker.dependencyInjection.viewModel
import com.example.baki_tracker.model.workout.Workout
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
        OptionsScreen(
            uiState = uiState,
            onEditClick = viewModel::onEditClick,
            onDeleteClick = viewModel::onDeleteClick,
            onDeleteConfirmation = viewModel::onDeleteConfirmation,
            hideDeleteDialog = viewModel::hideDeleteDialog
        )

    }

}

@Composable
fun OptionsScreen(
    uiState: OptionsUiState,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDeleteConfirmation: (String) -> Unit,
    hideDeleteDialog: () -> Unit
) {
    if (uiState.selectedWorkout != null) {
        Column {
            //Header
            OptionsHeader(uiState.selectedWorkout)
            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Column(Modifier.padding(horizontal = 24.dp)) {
                OptionsItem(Icons.Outlined.Edit, stringResource(R.string.edit_workout), onEditClick)
                OptionsItem(
                    Icons.Outlined.Delete, stringResource(R.string.delete_workout), onDeleteClick
                )
            }
        }
        ConfirmDeletionDialog(
            uiState.showDeleteDialog,
            uiState.selectedWorkout.uuid,
            onDeleteConfirmation,
            hideDeleteDialog
        )
    } else {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier.width(24.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

@Composable
fun ConfirmDeletionDialog(
    isVisible: Boolean, uuid: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit
) {
    if (isVisible) {
        AlertDialog(onDismissRequest = { onDismiss() }, // Called when the user taps outside or presses back
            confirmButton = {
                TextButton(onClick = { onConfirm(uuid) }) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            }, dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
            }, title = { Text(text = "${stringResource(R.string.delete_workout)}?") }, text = {
                Text(
                    text = stringResource(R.string.delete_workout_confirmation)
                )
            })
    }
}

@Composable
fun OptionsItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, "")
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}

@Composable
fun OptionsHeader(workout: Workout) {
    Column(
        Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
    ) {
        Text(
            workout.name, style = MaterialTheme.typography.labelMedium, fontSize = TextUnit(
                16f, TextUnitType.Sp
            )
        )
        Row {
            Text(workout.workoutType.value)
            Text("-", modifier = Modifier.padding(horizontal = 8.dp))
            Text("${workout.exercises.size} ${stringResource(R.string.exercises)}")
        }
    }
}

@Preview
@Composable
fun OptionsPreview() {
    OptionsScreen(OptionsUiState.initialUiState(), {}, {}, {}, {})
}

@Preview
@Composable
fun OptionsRowPreview() {
    Column {
        OptionsItem(Icons.Default.Delete, "Delete", {})
        OptionsItem(Icons.Default.Edit, "Delete", {})
    }
}


