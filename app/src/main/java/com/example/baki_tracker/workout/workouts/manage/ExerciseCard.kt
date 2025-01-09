package com.example.baki_tracker.workout.workouts.manage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baki_tracker.R
import com.example.baki_tracker.model.workout.WorkoutSet
import java.util.UUID

@Composable
fun ExerciseCard(
    exerciseName: String,
    exerciseNumber: Int,
    exerciseId: String,
    sets: List<WorkoutSet>,
    isCreating: Boolean = false,
    onExerciseNameChange: (String, String) -> Unit,
    onSetChange: (exerciseId: String, setId: String, newReps: Int, newWeight: Double) -> Unit,
    onAddSetToExercise: (String) -> Unit,
    onDeleteSetFromExercise: (String, String) -> Unit,
    onDeleteExercise: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            val name = if (isCreating) "Exercise $exerciseNumber" else exerciseName
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(modifier = Modifier.weight(1f), fontSize = 22.sp, text = name)
                Icon(
                    imageVector = Icons.Default.Close,
                    "",
                    modifier = Modifier.clickable { onDeleteExercise(exerciseId) })
            }

            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isCreating) OutlinedTextField(
                    value = exerciseName,
                    onValueChange = { onExerciseNameChange(exerciseId, it) },
                    label = { Text(text = "Exercise Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            sets.forEachIndexed { index, set ->
                SetRow(
                    exerciseId,
                    set.uuid,
                    index + 1,
                    set.reps,
                    set.weight,
                    onSetChange = onSetChange,
                    onDeleteSetFromExercise
                )
                if (index < sets.lastIndex) Spacer(Modifier.height(8.dp))
            }


            Button(
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = { onAddSetToExercise(exerciseId) }) {
                Text(text = stringResource(R.string.add_set))
            }
        }

    }
}


@Composable
fun SetRow(
    exerciseId: String,
    setId: String,
    setNumber: Int,
    reps: Int,
    weight: Double,
    onSetChange: (exerciseId: String, setId: String, newReps: Int, newWeight: Double) -> Unit,
    onDeleteSetFromExercise: (String, String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Set $setNumber", fontSize = 22.sp, modifier = Modifier.padding(end = 8.dp))
        OutlinedTextField(
            value = reps.toString(),
            onValueChange = { onSetChange(exerciseId, setId, it.toIntOrNull() ?: 0, weight) },
            keyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            label = { Text(text = stringResource(R.string.reps)) },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
        )

        OutlinedTextField(
            value = weight.toString(),
            onValueChange = {
                onSetChange(exerciseId, setId, reps, it.toDoubleOrNull() ?: 0.0)
            },
            keyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            label = { Text(text = stringResource(R.string.weight)) },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
        )
        Icon(
            imageVector = Icons.Default.Delete,
            "",
            Modifier.clickable { onDeleteSetFromExercise(exerciseId, setId) })

    }
}

@Preview
@Composable
fun ExerciseCardPreview() {
    val list = listOf(
        WorkoutSet(UUID.randomUUID().toString(), 14, 23.4),
        WorkoutSet(UUID.randomUUID().toString(), 14, 23.4),
        WorkoutSet(UUID.randomUUID().toString(), 14, 23.4)
    )
    ExerciseCard(
        "Exercise 1",
        exerciseNumber = 2,
        UUID.randomUUID().toString(),
        onExerciseNameChange = { _, _ -> },
        onSetChange = { _, _, _, _ -> },
        isCreating = false,
        onDeleteExercise = { _ -> },
        onAddSetToExercise = { _ -> },
        sets = list,
        onDeleteSetFromExercise = { _, _ -> }
    )

}