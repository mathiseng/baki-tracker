package com.example.baki_tracker.workout.tracking

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.baki_tracker.R
import com.example.baki_tracker.dependencyInjection.viewModel
import me.tatarka.inject.annotations.Inject

typealias TrackingScreen = @Composable () -> Unit

@Inject
@Composable
fun TrackingScreen(trackingViewModel: () -> TrackingViewModel) {

    val viewModel = viewModel { trackingViewModel() }

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {

            itemsIndexed(uiState.sessionList) { index, session ->
                TrackingOverviewCard(session) { }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        Button({ viewModel.onTrackWorkout() }) { Text(stringResource(R.string.track_workout)) }

        if (uiState.showTrackingDialog) {
            SelectionDialog(
                uiState = uiState,
                onDismiss = viewModel::onDismissDialog,
                onStartFreeWorkout = viewModel::onStartFreeWorkout,
                onStartPredefinedWorkout = viewModel::onStartPredefinedWorkout
            )
        }
    }
}