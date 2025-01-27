package com.example.baki_tracker.workout.planning

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.baki_tracker.R
import com.example.baki_tracker.dependencyInjection.viewModel
import com.example.baki_tracker.model.workout.PlannedWorkout
import me.tatarka.inject.annotations.Inject

typealias PlanningScreen = @Composable () -> Unit


@Inject
@Composable
fun PlanningScreen(planningViewModel: () -> PlanningViewModel) {

    val viewModel = viewModel { planningViewModel() }

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        //.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Planned Workouts",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.W400,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            uiState.plannedMap.forEach { (date, plannedWorkouts) ->

                item {
                    if (viewModel.currentDateString == date) {
                        Text(
                            text = stringResource(R.string.today),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    } else {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }

                itemsIndexed(plannedWorkouts) { index, item ->
                    PlanningCard(item,
                        { viewModel.onOptionsSelected(item) },
                        { viewModel.onStartWorkout(item.workoutId) })
                    Spacer(Modifier.height(16.dp))

                    if (index == plannedWorkouts.lastIndex) {
                        Spacer(Modifier.height(16.dp))
                    }
                }

                item {
                    if (viewModel.currentDateString == date) {
                        HorizontalDivider()
                        Text(
                            text = "Upcoming Workouts",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.W400,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

            }
        }

        Button(
            onClick = { viewModel.onShowPlanBottomSheetChange(true) },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
        ) { Text("Plan Workout") }
    }
}

@Composable
fun PlanningCard(
    plannedWorkout: PlannedWorkout, onOptionsSelected: () -> Unit, onStartWorkout: () -> Unit
) {
    ElevatedCard(elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = plannedWorkout.title,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${plannedWorkout.startTime} - ${plannedWorkout.endTime}",
                        fontWeight = FontWeight.Light,
                        fontSize = TextUnit(12.0F, TextUnitType.Sp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = plannedWorkout.description,
                        fontWeight = FontWeight.Light,
                        fontSize = TextUnit(12.0F, TextUnitType.Sp),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier.align(Alignment.Top), horizontalAlignment = Alignment.End
                ) {
                    Icon(imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        modifier = Modifier.clickable { onOptionsSelected() })
                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = onStartWorkout, modifier = Modifier.align(Alignment.End)
                    ) { Text("Start") }

                }
            }
        }
    }
}