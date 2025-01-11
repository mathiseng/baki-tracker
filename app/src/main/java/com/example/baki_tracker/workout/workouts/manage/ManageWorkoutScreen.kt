package com.example.baki_tracker.workout.workouts.manage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baki_tracker.R
import com.example.baki_tracker.model.workout.WorkoutExercise
import com.example.baki_tracker.model.workout.WorkoutSet
import com.example.baki_tracker.model.workout.WorkoutType
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageWorkoutScreen(
    modifier: Modifier = Modifier,
    uiState: ManageWorkoutUiState,
    onAddExercise: () -> Unit,
    onDeleteExercise: (String) -> Unit,
    onExerciseNameChange: (String, String) -> Unit,
    onSetChange: (exerciseId: String, setId: String, newReps: Int, newWeight: Double) -> Unit,
    onAddSetToExercise: (String) -> Unit,
    onDeleteSetFromExercise: (String, String) -> Unit,
    onWorkoutNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSaveWorkout: () -> Unit,
    onWorkoutTypeChange: (WorkoutType) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        //HEADER
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            fontSize = 22.sp,
            text = if (uiState.isCreatingWorkout) stringResource(R.string.add_workout) else stringResource(R.string.edit_workout)
        )

        Column(
            modifier = modifier
                .weight(1f)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())

        ) {
            OutlinedTextField(
                value = uiState.workoutName,
                onValueChange = {
                    onWorkoutNameChange(it)
                },
                label = { Text(text = stringResource(R.string.workout_name)) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(4.dp))

            //Dropdown
            var isDropdownExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                modifier = Modifier.padding(bottom = 4.dp),
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
            ) {
                Button(modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true), colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface
                ), shape = RoundedCornerShape(12.dp),

                    onClick = { isDropdownExpanded = true }) {
                    Text(
                        text = if (uiState.workoutType != null) uiState.workoutType.value else stringResource(
                            R.string.select_workout_type
                        )
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "expand more"
                    )
                }

                DropdownMenu(modifier = Modifier.align(Alignment.End),
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }) {
                    WorkoutType.getAllWorkoutTypes().forEach { type ->
                        DropdownMenuItem(text = { Text(type.value) }, onClick = {
                            onWorkoutTypeChange(type)
                            isDropdownExpanded = false
                        })

                    }
                }
            }

            //Exercises
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                uiState.exercises.forEachIndexed { index, workoutExercise ->
                    ExerciseCard(
                        exerciseName = workoutExercise.name,
                        exerciseNumber = index + 1,
                        exerciseId = workoutExercise.uuid,
                        sets = workoutExercise.sets,
                        isCreating = true,
                        onExerciseNameChange = onExerciseNameChange,
                        onSetChange = onSetChange,
                        onAddSetToExercise = onAddSetToExercise,
                        onDeleteExercise = onDeleteExercise,
                        onDeleteSetFromExercise = onDeleteSetFromExercise
                    )
                    if (index < uiState.exercises.lastIndex) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Button(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .padding(vertical = 8.dp),
                    onClick = onAddExercise
                ) {
                    Text(text = stringResource(R.string.add_exercise))
                }
            }
        }

        HorizontalDivider()
        //Submit Buttons
        Row(modifier = Modifier.padding(vertical = 16.dp)) {
            Button(onClick = { onSaveWorkout() }) { Text(stringResource(R.string.save)) }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = { onDismiss() }) { Text(stringResource(R.string.cancel)) }

        }
    }
}

@Preview(backgroundColor = 0xFFFF, showBackground = true)
@Composable
fun ManageWorkoutScreenPreview() {
    val sets = listOf(
        WorkoutSet(UUID.randomUUID().toString(), 14, 23.4),
        WorkoutSet(UUID.randomUUID().toString(), 14, 23.4),
        WorkoutSet(UUID.randomUUID().toString(), 14, 23.4)
    )
    val list = listOf(
        WorkoutExercise(UUID.randomUUID().toString(), "Bankdrücken", sets),

        )
    ManageWorkoutScreen(
        uiState = ManageWorkoutUiState(
            "", null, list, true
        ),
        onExerciseNameChange = { _, _ -> },
        onSetChange = { _, _, _, _ -> },
        onAddExercise = {},
        onDeleteExercise = { _ -> },
        onAddSetToExercise = { _ -> },
        onDeleteSetFromExercise = { _, _ -> },
        onDismiss = {},
        onSaveWorkout = {},
        onWorkoutNameChange = {},
        onWorkoutTypeChange = {},
    )
}