package com.example.baki_tracker.workout.workouts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.baki_tracker.R
import com.example.baki_tracker.model.dummydata.DummyData
import com.example.baki_tracker.model.workout.Workout

@Composable
fun WorkoutOverviewCard(
    workout: Workout,
    onOptionsSelected: () -> Unit,
    onStartWorkout: () -> Unit
) {

    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Column {
                    Text(
                        text = workout.name,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))



                    Text(
                        text = "${workout.exercises.size} ${stringResource(R.string.exercises)}",
                        fontWeight = FontWeight.Light,
                        fontSize = TextUnit(12.0F, TextUnitType.Sp),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(Modifier.weight(1f)) {
                    SuggestionChip(modifier = Modifier.height(24.dp),
                        enabled = false,
                        colors = SuggestionChipDefaults.suggestionChipColors(disabledLabelColor = CardDefaults.cardColors().contentColor),
                        border = BorderStroke(0.5.dp, CardDefaults.cardColors().contentColor),
                        onClick = {},
                        label = {
                            workout.workoutType.value.let {
                                Text(
                                    text = it,
                                    fontWeight = FontWeight.W400,
                                    fontSize = TextUnit(10.0F, TextUnitType.Sp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        })
                }

                Icon(imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Top)
                        .clickable { onOptionsSelected() } // Aligns the Icon to the top of the Row
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onStartWorkout() }, modifier = Modifier
                .height(40.dp)
                .fillMaxWidth(), shape = RectangleShape
        ) { Text("Start") }
    }
}

@Preview
@Composable
fun WorkoutOverviewCardPreview() {


    WorkoutOverviewCard(workout = DummyData.workout, onOptionsSelected = {}, onStartWorkout = {})
}