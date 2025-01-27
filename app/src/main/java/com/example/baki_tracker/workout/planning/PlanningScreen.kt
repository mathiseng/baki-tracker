package com.example.baki_tracker.workout.planning

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baki_tracker.R
import com.example.baki_tracker.dependencyInjection.viewModel
import com.example.baki_tracker.model.workout.PlannedWorkout
import com.example.baki_tracker.model.workout.Workout
import com.example.baki_tracker.utils.formatLongDateToFormattedDateString
import com.example.baki_tracker.workout.tracking.PredefinedWorkoutList
import me.tatarka.inject.annotations.Inject
import java.util.Calendar

typealias PlanningScreen = @Composable () -> Unit


@Inject
@Composable
fun PlanningScreen(planningViewModel: () -> PlanningViewModel) {

    val viewModel = viewModel { planningViewModel() }

    val uiState by viewModel.uiState.collectAsState()
    if (uiState.showPlanningBottomSheet) PlanBottomSheet(
        uiState, { viewModel.onShowPlanBottomSheetChange(false) }, viewModel::onPlanWorkout
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        //.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Planned Workouts",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Light,
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
                    PlanningCard(item)
                    Spacer(Modifier.height(16.dp))

                    if (index == plannedWorkouts.lastIndex) {
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }

        Button(onClick = { viewModel.onShowPlanBottomSheetChange(true) }) { Text("Plan Workout") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanBottomSheet(
    uiState: PlanningUiState, onDismiss: () -> Unit, onPlanWorkout: (Long, Long, Workout) -> Unit
) {

    val sheetState = rememberModalBottomSheetState(
        confirmValueChange = {
            SheetValue.Hidden != it
        }, skipPartiallyExpanded = true
    )


    ModalBottomSheet(
        modifier = Modifier.statusBarsPadding(), onDismissRequest = { }, sheetState = sheetState
    ) {
        PlanBottomSheetContent(uiState, onDismiss, onPlanWorkout)

        BackHandler {
            onDismiss()
        }
    }
}

@Composable
fun PlanBottomSheetContent(
    uiState: PlanningUiState, onDismiss: () -> Unit, onPlanWorkout: (Long, Long, Workout) -> Unit
) {
    Column(
        Modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 8.dp)
    ) {
        var selectedDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
        var selectedStartTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
        var showDatePicker by remember { mutableStateOf(false) }
        var showTimePicker by remember { mutableStateOf(false) }
        var selectedWorkout: Workout? by remember { mutableStateOf(null) }


        if (showDatePicker) DatePickerModal({ it?.let { selectedDate = it } }) {
            showDatePicker = false
        }

        if (showTimePicker) TimePickerDialog({ it?.let { selectedStartTime = it } }) {
            showTimePicker = false
        }

        Text(
            modifier = Modifier.padding(bottom = 16.dp), fontSize = 22.sp, text = "Plan Workout"
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Date", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(
                modifier = Modifier.clickable { showDatePicker = true },
                text = formatLongDateToFormattedDateString(selectedDate),
            )
        }
        Spacer(Modifier.height(8.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Start Time",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                modifier = Modifier.clickable { showTimePicker = true },
                text = formatLongDateToFormattedDateString(selectedStartTime, "HH:mm"),
            )
        }
        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = "Selected Workout",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Column(
            Modifier.border(
                1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)
            )
        ) {

            PredefinedWorkoutList(modifier = Modifier
                .fillMaxHeight(0.5f)
                .padding(vertical = 8.dp),
                workoutList = uiState.workoutList,
                selectedWorkout = selectedWorkout,
                onChangeSelection = { selectedWorkout = it })
        }
        Spacer(Modifier.height(16.dp))

        HorizontalDivider()
        //Submit Buttons
        Row(modifier = Modifier.padding(vertical = 16.dp)) {
            Button(enabled = selectedWorkout != null, onClick = {
                selectedWorkout?.let {
                    onPlanWorkout(
                        selectedDate, selectedStartTime, it
                    )
                }
            }) { Text(stringResource(R.string.save)) }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit, onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = {
            onDateSelected(datePickerState.selectedDateMillis)
            onDismiss()
        }) {
            Text("OK")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TimePickerDialog(
    onConfirm: (Long?) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    AlertDialog(onDismissRequest = { onDismiss() }, confirmButton = {
        TextButton(onClick = {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
            cal.set(Calendar.MINUTE, timePickerState.minute)
            onConfirm(cal.timeInMillis)
            onDismiss()
        }) {
            Text("Save")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }, title = { Text(text = "Select") }, text = {

        Column {
            TimePicker(
                state = timePickerState,
            )
        }
    })
}


@Composable
fun PlanningCard(
    plannedWorkout: PlannedWorkout,
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
                        text = "5 Exercises",
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
                        modifier = Modifier.clickable { })
                    Spacer(modifier = Modifier.height(4.dp))

                    Button(onClick = {}, modifier = Modifier.align(Alignment.End)) { Text("Start") }

                }
            }
        }
    }
}