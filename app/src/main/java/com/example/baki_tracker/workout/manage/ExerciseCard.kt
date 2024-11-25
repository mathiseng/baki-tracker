package com.example.baki_tracker.workout.manage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baki_tracker.model.workout.WorkoutSet

@Composable
fun ExerciseCard(
    exerciseName: String,
    sets: List<WorkoutSet>,
    isCreating: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(modifier = Modifier.weight(1f), fontSize = 22.sp, text = exerciseName)

                Icon(imageVector = Icons.Default.Close, "")
            }

            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isCreating) OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text(text = "Exercise Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            sets.forEach {
                SetRow(it.setNumber, it.reps, it.weight, onSetChange = { _, _ -> })
            }


            Button(
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {}) {
                Text(text = "add set")
            }
        }

    }
}


@Composable
fun SetRow(setNumber: Int, reps: Int, weight: Double, onSetChange: (String, String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "Set $setNumber", fontSize = 22.sp, modifier = Modifier.padding(end = 8.dp))
        OutlinedTextField(
            value = reps.toString(),
            onValueChange = { onSetChange(it, weight.toString()) },
            label = { Text(text = "Reps") },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
        )

        OutlinedTextField(
            value = weight.toString(),
            onValueChange = { onSetChange(reps.toString(), it) },
            label = { Text(text = "Weight") },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
        )
        Icon(imageVector = Icons.Default.Delete, "")

    }
}

@Preview
@Composable
fun ExerciseCardPreview() {
    val list = listOf(WorkoutSet(1, 14, 23.4), WorkoutSet(2, 14, 23.4), WorkoutSet(3, 14, 23.4))
    ExerciseCard("Exercise 1", list)

}