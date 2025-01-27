package com.example.baki_tracker.workout.planning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baki_tracker.model.workout.PlannedWorkout
import com.example.baki_tracker.model.workout.Workout
import com.example.baki_tracker.repository.GoogleAuthState
import com.example.baki_tracker.repository.IGoogleRepository
import com.example.baki_tracker.repository.IWorkoutDatabaseRepository
import com.example.baki_tracker.utils.formatDateToUTC
import com.example.baki_tracker.utils.getCurrentDateString
import com.example.baki_tracker.workout.ISharedWorkoutStateRepository
import com.example.baki_tracker.workout.WorkoutBottomSheet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import java.util.Calendar

@Inject
class PlanningViewModel(
    private val workoutDatabaseRepository: IWorkoutDatabaseRepository,
    private val sharedWorkoutStateRepository: ISharedWorkoutStateRepository,
    private val googleRepository: IGoogleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlanningUiState())
    val uiState: StateFlow<PlanningUiState> = _uiState
    val currentDateString = getCurrentDateString()

    init {
        viewModelScope.launch {
            googleRepository.plannedWorkouts.collect { list ->
                _uiState.update { it.copy(plannedMap = groupPlannedWorkoutsByDate(list)) }

            }
        }


        viewModelScope.launch {
            googleRepository.authState.collect { authState ->
                _uiState.update { it.copy(isAuthenticated = authState == GoogleAuthState.Authenticated) }

            }
        }

        viewModelScope.launch {
            workoutDatabaseRepository.workouts.collect { list ->
                _uiState.update { it.copy(workoutList = list) }
            }
        }

        viewModelScope.launch {
            sharedWorkoutStateRepository.selectedPlannedWorkout.collect { plannedWorkout ->
                _uiState.update { it.copy(selectedPlannedWorkout = plannedWorkout) }
            }
        }
    }


    fun refreshCalendarEvents() {
        viewModelScope.launch {
            googleRepository.getCalendarEvents()
        }
    }

    fun onShowPlanBottomSheetChange(show: Boolean) {
        sharedWorkoutStateRepository.updateSelectedBottomSheet(WorkoutBottomSheet.ADD_PLANNED)
        //_uiState.update { it.copy(showPlanningBottomSheet = show) }
    }

    fun onPlanWorkout(date: Long, startTime: Long, workout: Workout, isEditing: Boolean = false) {
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

            if (isEditing) {
                uiState.value.selectedPlannedWorkout?.let {
                    googleRepository.updateCalendarEvent(
                        it.eventId,
                        startTimeUTC,
                        endTimeUTC,
                        workout
                    )
                }
            } else googleRepository.planCalendarEvent(startTimeUTC, endTimeUTC, workout)

            sharedWorkoutStateRepository.dismissBottomSheet()
            sharedWorkoutStateRepository.updateSelectedPlannedWorkout(null)
        }
    }

    fun onDismiss() {
        sharedWorkoutStateRepository.dismissBottomSheet()
        sharedWorkoutStateRepository.updateSelectedPlannedWorkout(null)
    }

    private fun groupPlannedWorkoutsByDate(items: List<PlannedWorkout>): Map<String, List<PlannedWorkout>> {
        return items.groupBy { it.date }.toSortedMap(compareBy { it })
    }

    fun onOptionsSelected(plannedWorkout: PlannedWorkout) {
        sharedWorkoutStateRepository.updateSelectedPlannedWorkout(plannedWorkout)
        sharedWorkoutStateRepository.updateSelectedBottomSheet(WorkoutBottomSheet.OPTIONS)
    }

    fun onStartWorkout(workoutId: String) {
        sharedWorkoutStateRepository.updateSelectedBottomSheet(WorkoutBottomSheet.TRACK)
        val workout = uiState.value.workoutList.firstOrNull() { it.uuid == workoutId }
        workout?.let {
            sharedWorkoutStateRepository.updateSelectedWorkout(it)
        }
    }

    fun onSignUpWithGoogle() {
        viewModelScope.launch {
            googleRepository.getAuthRequest()
        }
    }
}