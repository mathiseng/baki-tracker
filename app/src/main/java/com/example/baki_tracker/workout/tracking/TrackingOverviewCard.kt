package com.example.baki_tracker.workout.tracking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.baki_tracker.R
import com.example.baki_tracker.model.dummydata.DummyData
import com.example.baki_tracker.model.workout.WorkoutTrackingSession

@Composable
fun TrackingOverviewCard(
    session: WorkoutTrackingSession,
    onShowDetails: () -> Unit,
    onOptionsSelected: () -> Unit,
) {

    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onShowDetails() }
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
                        text = session.name,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${session.trackedExercises.size} ${stringResource(R.string.exercises)}",
                        fontWeight = FontWeight.Light,
                        fontSize = TextUnit(12.0F, TextUnitType.Sp),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                Icon(imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Top)
                        .clickable { onOptionsSelected() }
                )
            }
        }
    }
}

@Preview
@Composable
fun WorkoutOverviewCardPreview() {
    TrackingOverviewCard(session = DummyData.workoutTrackingSession, onOptionsSelected = {}, onShowDetails = {})
}