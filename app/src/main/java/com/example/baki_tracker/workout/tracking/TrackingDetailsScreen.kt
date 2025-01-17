package com.example.baki_tracker.workout.tracking

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baki_tracker.R
import com.example.baki_tracker.model.workout.TrackedWorkoutSet
import com.example.baki_tracker.model.workout.Workout
import com.example.baki_tracker.model.workout.WorkoutSet
import com.example.baki_tracker.model.workout.WorkoutTrackingSession
import com.example.baki_tracker.utils.formatTimestampToString
import com.example.components.scrollbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingDetailsScreen(
    session: WorkoutTrackingSession, relatedWorkout: Workout?, onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        modifier = Modifier.statusBarsPadding(),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                fontSize = 22.sp,
                text = "Tracked Workout"
            )
            Text(
                fontSize = 16.sp, text = "Name: " + session.name
            )

            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                fontSize = 16.sp,
                text = "Date: " + session.date.formatTimestampToString()
            )
            HorizontalDivider(Modifier.height(16.dp))

            LazyColumn {
                itemsIndexed(session.trackedExercises) { index, exercise ->
                    val plannedSets: List<WorkoutSet> =
                        if (relatedWorkout != null && index <= relatedWorkout.exercises.lastIndex) relatedWorkout.exercises[index].sets else emptyList()
                    DetailsSection(exercise.name, exercise.sets, plannedSets)
                    HorizontalDivider(Modifier.padding(8.dp))
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        BackHandler {
            onDismiss()
        }
    }
}

@Composable
fun DetailsSection(name: String, sets: List<TrackedWorkoutSet>, plannedSets: List<WorkoutSet>) {
    Column() {
        Text(modifier = Modifier.padding(0.dp), fontSize = 22.sp, text = name)
        val listState = rememberLazyListState()

        LazyRow(
            state = listState,
            modifier = Modifier
                .padding(horizontal = 0.dp)
                .padding(top = 8.dp)
                .fillMaxWidth()
                .scrollbar(listState, horizontal = true, alignEnd = true)
        ) {
            item {
                // Spalte für Set-Nummer
                DetailsColumn(stringResource(R.string.set),
                    sets.size,
                    List(sets.size) { index -> (index + 1).toString() })
                Spacer(Modifier.width(16.dp))

                // Spalte für geplante Wiederholungen
                DetailsColumn(
                    stringResource(R.string.planned),
                    sets.size,
                    plannedSets.map { "${it.reps}" })
                Spacer(Modifier.width(16.dp))

                // Spalte für tatsächliche Wiederholungen
                DetailsColumn(stringResource(R.string.reps), sets.size, sets.map { "${it.reps}" })
                Spacer(Modifier.width(16.dp))

                // Spalte für geplantes Gewicht
                DetailsColumn(
                    stringResource(R.string.planned),
                    sets.size,
                    plannedSets.map { "${it.weight}" })
                Spacer(Modifier.width(16.dp))
                // Spalte für tatsächliches Gewicht
                DetailsColumn(
                    stringResource(R.string.weight),
                    sets.size,
                    sets.map { "${it.weight}" })
            }
        }
    }
}

@Composable
fun DetailsColumn(title: String, columnSize: Int, valueList: List<String>) {
    Column(horizontalAlignment = Alignment.End) {
        Text(title, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        repeat(columnSize) { index ->
            val value = if (index <= valueList.lastIndex) valueList[index] else "0"
            Text(value)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}