package com.example.baki_tracker.workout.planning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baki_tracker.model.workout.PlannedWorkout
import com.example.baki_tracker.model.workout.Workout
import com.example.baki_tracker.repository.IGoogleRepository
import com.example.baki_tracker.repository.IWorkoutDatabaseRepository
import com.example.baki_tracker.utils.formatDateToUTC
import com.example.baki_tracker.utils.getCurrentDateString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import java.util.Calendar

@Inject
class PlanningViewModel(
    val workoutDatabaseRepository: IWorkoutDatabaseRepository,
    val googleRepository: IGoogleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlanningUiState())
    val uiState: StateFlow<PlanningUiState> = _uiState
    val currentDateString = getCurrentDateString()

    init {
        viewModelScope.launch {
            googleRepository.getCalendarEvents()
        }

        viewModelScope.launch {

            googleRepository.plannedWorkouts.collect { list ->
                _uiState.update { it.copy(plannedMap = groupPlannedWorkoutsByDate(list)) }

            }
        }

        viewModelScope.launch {
            workoutDatabaseRepository.workouts.collect { list ->
                _uiState.update { it.copy(workoutList = list) }
            }
        }
    }

    fun onShowPlanBottomSheetChange(show: Boolean) {
        _uiState.update { it.copy(showPlanningBottomSheet = show) }
    }

    fun onPlanWorkout(date: Long, startTime: Long, workout: Workout) {
        viewModelScope.launch {
            val dateCalendar = Calendar.getInstance()
            dateCalendar.timeInMillis = date

            val timeCalendar = Calendar.getInstance()
            timeCalendar.timeInMillis = startTime
            // Apply the hours and minutes from startTime to the selected date
            dateCalendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
            dateCalendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))

            val endCalendar = Calendar.getInstance()
            endCalendar.timeInMillis =
                dateCalendar.timeInMillis // Start with the same time as startTime
            endCalendar.add(Calendar.HOUR_OF_DAY, 2) // Add the duration to hours

            // Save the end time in UTC format
            val startTimeUTC = formatDateToUTC(dateCalendar.time)
            val endTimeUTC = formatDateToUTC(endCalendar.time)
            googleRepository.planCalendarEvent(startTimeUTC, endTimeUTC, workout)
        }
    }

    private fun groupPlannedWorkoutsByDate(items: List<PlannedWorkout>): Map<String, List<PlannedWorkout>> {
        return items.groupBy { it.date }.toSortedMap(compareByDescending { it })
    }
}