package com.example.baki_tracker.workout.manage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baki_tracker.R
import com.example.baki_tracker.model.workout.WorkoutExercise
import com.example.baki_tracker.model.workout.WorkoutSet
import java.util.UUID

@Composable
fun ManageWorkoutScreen(
    modifier: Modifier = Modifier,
    uiState: ManageWorkoutUiState,
    onAddExercise: () -> Unit,
    onDeleteExercise: (String) -> Unit,
    onExerciseNameChange: (String, String) -> Unit,
    onSetChange: (exerciseId: String, setId: String, newReps: Int, newWeight: Double) -> Unit,
    onAddSetToExercise: (String) -> Unit,
    onDeleteSetFromExercise: (String, String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
    ) {
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            fontSize = 22.sp,
            text = stringResource(R.string.add_workout)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(uiState.exercises) { index, workoutExercise ->
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

            item {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .padding(vertical = 8.dp), onClick = onAddExercise
                ) {
                    Text(text = stringResource(R.string.add_exercise))
                }
            }
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
            list
        ),
        onExerciseNameChange = { _, _ -> },
        onSetChange = { _, _, _, _ -> },
        onAddExercise = {},
        onDeleteExercise = { _ -> },
        onAddSetToExercise = { _ -> },
        onDeleteSetFromExercise = { _, _ -> }
    )
}