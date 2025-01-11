package com.example.baki_tracker.workout.workouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.baki_tracker.dependencyInjection.viewModel
import me.tatarka.inject.annotations.Inject

typealias WorkoutsScreen = @Composable () -> Unit

@Inject
@Composable
fun WorkoutsScreen(workoutsViewModel: () -> WorkoutsViewModel) {

    val viewModel = viewModel { workoutsViewModel() }

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(uiState.workoutList) { index, workout ->
                WorkoutOverviewCard(workout) { viewModel.onOptionsSelected(workout) }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        Button({ viewModel.onAddWorkout() }) { Text("Add Workout") }
    }
}