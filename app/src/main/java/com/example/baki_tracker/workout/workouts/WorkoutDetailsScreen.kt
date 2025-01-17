package com.example.baki_tracker.workout.workouts

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baki_tracker.R
import com.example.baki_tracker.model.dummydata.DummyData
import com.example.baki_tracker.model.workout.Workout
import com.example.baki_tracker.model.workout.WorkoutSet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailsScreen(workout: Workout, onDismiss: () -> Unit) {

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        modifier = Modifier.statusBarsPadding(),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {

            Text(modifier = Modifier.padding(bottom = 16.dp), fontSize = 22.sp, text = workout.name)

            LazyColumn {
                itemsIndexed(workout.exercises) { index, exercise ->
                    DetailsCard(exercise.name, exercise.sets)
                    Spacer(Modifier.height(16.dp))
                }
            }
        }

        BackHandler {
            onDismiss()
        }
    }
}

@Composable
fun DetailsCard(name: String, sets: List<WorkoutSet>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column() {
            Text(modifier = Modifier.padding(16.dp), fontSize = 22.sp, text = name)
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.set), fontWeight = FontWeight.Medium)
                        Text(stringResource(R.string.reps), fontWeight = FontWeight.Medium)
                        Text(stringResource(R.string.weight), fontWeight = FontWeight.Medium)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    sets.forEachIndexed() { index, set ->
                        DetailsRow(index + 1, set.reps, set.weight)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DetailsRow(setNumber: Int, reps: Int, weight: Double) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("$setNumber")
        Text("$reps")
        Text("$weight")
    }
}

@Preview
@Composable
fun DetailsCardPreview() {
    DetailsCard("BenchPress", DummyData.workouts[1].exercises[0].sets)
}

@Preview
@Composable
fun DetailsScreenPreview() {
    WorkoutDetailsScreen(DummyData.workout) { }
}


