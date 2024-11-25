package com.example.baki_tracker.workout.manage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baki_tracker.model.workout.WorkoutExercise
import com.example.baki_tracker.model.workout.WorkoutSet

@Composable
fun ManageWorkoutScreen(
    modifier: Modifier = Modifier,
    uiState: ManageWorkoutUiState,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(24.dp)
    ) {
        Text(modifier = Modifier.padding(bottom = 16.dp), fontSize = 22.sp, text = "Add Workout")

        LazyColumn {
            itemsIndexed(uiState.exercises) { index, workoutExercise ->
                ExerciseCard(workoutExercise.name, workoutExercise.sets, false)
                if (index < uiState.exercises.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {}) {
            Text(text = "add exercise")
        }
    }
}

@Preview(backgroundColor = 0xFFFF, showBackground = true)
@Composable
fun ManageWorkoutScreenPreview() {
    val sets = listOf(WorkoutSet(1, 14, 23.4), WorkoutSet(2, 14, 23.4), WorkoutSet(3, 14, 23.4))
    val list = listOf(
        WorkoutExercise(2, "Bankdrücken", sets),

        )
    ManageWorkoutScreen(
        uiState = ManageWorkoutUiState(
            list
        )
    )
}