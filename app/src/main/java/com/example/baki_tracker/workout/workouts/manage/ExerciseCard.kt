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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baki_tracker.R
import com.example.baki_tracker.model.workout.WorkoutExercise
import com.example.baki_tracker.model.workout.WorkoutSet
import com.example.baki_tracker.utils.DecimalInputVisualTransformation
import com.example.baki_tracker.utils.formatFloatingPoint
import java.util.UUID

@Composable
fun ExerciseCard(
    plannedExercise: WorkoutExercise? = null,
    exerciseName: String,
    exerciseNumber: Int,
    exerciseId: String,
    sets: List<WorkoutSet>,
    manageWorkoutMode: ManageWorkoutMode = ManageWorkoutMode.CREATE,
    onExerciseNameChange: (String, String) -> Unit,
    onSetChange: (exerciseId: String, setId: String, newReps: Int, newWeight: Double) -> Unit,
    onAddSetToExercise: (String) -> Unit,
    onDeleteSetFromExercise: (String, String) -> Unit,
    onDeleteExercise: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            //Header
            val name =
                if (manageWorkoutMode != ManageWorkoutMode.TRACK) "Exercise $exerciseNumber" else exerciseName
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(modifier = Modifier.weight(1f), fontSize = 22.sp, text = name)
                Icon(imageVector = Icons.Default.Close,
                    "",
                    modifier = Modifier.clickable { onDeleteExercise(exerciseId) })
            }

            if (plannedExercise == null) OutlinedTextField(
                value = exerciseName,
                onValueChange = { onExerciseNameChange(exerciseId, it) },
                label = { Text(text = "Exercise Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )

            if (manageWorkoutMode != ManageWorkoutMode.TRACK) {
                ExerciseEditorScreen(
                    exerciseId = exerciseId,
                    sets = sets,
                    onSetChange = onSetChange,
                    onDeleteSetFromExercise = onDeleteSetFromExercise
                )
            } else {
                TrackingExerciseScreen(
                    plannedExercise = plannedExercise,
                    exerciseId = exerciseId,
                    sets = sets,
                    onSetChange = onSetChange,
                    onDeleteSetFromExercise = onDeleteSetFromExercise
                )
            }

            Button(modifier = Modifier
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
private fun ExerciseEditorScreen(
    exerciseId: String,
    sets: List<WorkoutSet>,
    onSetChange: (exerciseId: String, setId: String, newReps: Int, newWeight: Double) -> Unit,
    onDeleteSetFromExercise: (String, String) -> Unit,

    ) {
    Column {
        sets.forEachIndexed { index, set ->
            SetRow(
                plannedSet = null,
                exerciseId = exerciseId,
                setId = set.uuid,
                setNumber = index + 1,
                reps = set.reps,
                weight = set.weight,
                onSetChange = onSetChange,
                onDeleteSetFromExercise = onDeleteSetFromExercise
            )
            if (index < sets.lastIndex) Spacer(Modifier.height(8.dp))
        }

    }
}

@Composable
private fun TrackingExerciseScreen(
    exerciseId: String,
    plannedExercise: WorkoutExercise? = null,
    sets: List<WorkoutSet>,
    onSetChange: (exerciseId: String, setId: String, newReps: Int, newWeight: Double) -> Unit,
    onDeleteSetFromExercise: (String, String) -> Unit,
) {
    Column {

        sets.forEachIndexed { index, set ->
            SetRow(
                plannedSet = if (plannedExercise != null && index <= plannedExercise.sets.lastIndex) plannedExercise.sets[index] else null,
                exerciseId = exerciseId,
                setId = set.uuid,
                setNumber = index + 1,
                reps = set.reps,
                weight = set.weight,
                onSetChange = onSetChange,
                onDeleteSetFromExercise = onDeleteSetFromExercise
            )
            if (index < sets.lastIndex) Spacer(Modifier.height(16.dp))
        }

    }
}


@Composable
private fun SetRow(
    plannedSet: WorkoutSet?,
    exerciseId: String,
    setId: String,
    setNumber: Int,
    reps: Int,
    weight: Double,
    onSetChange: (exerciseId: String, setId: String, newReps: Int, newWeight: Double) -> Unit,
    onDeleteSetFromExercise: (String, String) -> Unit
) {
    val showPlannedValues = (plannedSet != null)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Set $setNumber", fontSize = 22.sp, modifier = Modifier.padding(end = 8.dp))

        var repsValue by remember { mutableStateOf(reps.toString()) }
        OutlinedTextField(
            value = repsValue,
            onValueChange = {
                repsValue = it
                val newReps = repsValue.toIntOrNull() ?: 0
                onSetChange(exerciseId, setId, newReps, weight)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
            label = { Text(text = stringResource(R.string.reps)) },
            supportingText = {
                if (showPlannedValues) Text(
                    "${stringResource(R.string.planned)}\n${plannedSet!!.reps}",
                )
            },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
        )

        var value by remember { mutableStateOf(weight.toString()) }
        OutlinedTextField(
            value = value,
            visualTransformation = DecimalInputVisualTransformation(),
            onValueChange = {
                value = formatFloatingPoint(it)
                val newDouble = value.replace(",", ".").toDoubleOrNull() ?: 0.0
                onSetChange(exerciseId, setId, reps, newDouble)

            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
            label = { Text(text = stringResource(R.string.weight)) },
            supportingText = {
                if (showPlannedValues) Text(
                    "${stringResource(R.string.planned)}\n${plannedSet!!.weight}",
                )
            },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
        )
        Icon(imageVector = Icons.Default.Delete,
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
    ExerciseCard(exerciseName = "Bench Press",
        exerciseNumber = 2,
        exerciseId = UUID.randomUUID().toString(),
        onExerciseNameChange = { _, _ -> },
        onSetChange = { _, _, _, _ -> },
        manageWorkoutMode = ManageWorkoutMode.EDIT,
        onDeleteExercise = { _ -> },
        onAddSetToExercise = { _ -> },
        sets = list,
        onDeleteSetFromExercise = { _, _ -> })

}