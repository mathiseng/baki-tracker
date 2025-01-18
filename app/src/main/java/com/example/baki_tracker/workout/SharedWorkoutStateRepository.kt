package com.example.baki_tracker.workout

import com.example.baki_tracker.dependencyInjection.Singleton
import com.example.baki_tracker.model.workout.Workout
import com.example.baki_tracker.model.workout.WorkoutTrackingSession
import com.example.baki_tracker.workout.components.DialogInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tatarka.inject.annotations.Inject

@Inject
@Singleton
class SharedWorkoutStateRepository : ISharedWorkoutStateRepository {
    private val _selectedWorkout: MutableStateFlow<Workout?> = MutableStateFlow(null)
    override val selectedWorkout = _selectedWorkout.asStateFlow()

    private val _selectedWorkoutTrackingSession: MutableStateFlow<WorkoutTrackingSession?> =
        MutableStateFlow(null)
    override val selectedWorkoutTrackingSession = _selectedWorkoutTrackingSession.asStateFlow()

    private val _selectedBottomSheet: MutableStateFlow<WorkoutBottomSheet> =
        MutableStateFlow(WorkoutBottomSheet.NONE)
    override val selectedBottomSheet = _selectedBottomSheet.asStateFlow()

    private val _dialog: MutableStateFlow<DialogInfo?> = MutableStateFlow(null)
    override val dialog = _dialog.asStateFlow()

    override fun updateSelectedWorkout(workout: Workout?) {
        _selectedWorkout.value = workout
    }

    override fun updateSelectedWorkoutTrackingSession(session: WorkoutTrackingSession?) {
        _selectedWorkoutTrackingSession.value = session
    }

    override fun updateSelectedBottomSheet(bottomSheet: WorkoutBottomSheet) {
        _selectedBottomSheet.value = bottomSheet
    }

    override fun updateDialog(dialogInfo: DialogInfo?) {
        _dialog.value = dialogInfo
    }

    override fun dismissBottomSheet() {
        _selectedBottomSheet.value = WorkoutBottomSheet.NONE
    }
}

interface ISharedWorkoutStateRepository {
    val selectedWorkout: StateFlow<Workout?>
    val selectedWorkoutTrackingSession: StateFlow<WorkoutTrackingSession?>
    val selectedBottomSheet: StateFlow<WorkoutBottomSheet>
    val dialog: StateFlow<DialogInfo?>

    fun updateSelectedWorkout(workout: Workout?)
    fun updateSelectedWorkoutTrackingSession(session: WorkoutTrackingSession?)
    fun updateSelectedBottomSheet(bottomSheet: WorkoutBottomSheet)
    fun updateDialog(dialogInfo: DialogInfo?)
    fun dismissBottomSheet()
}

enum class WorkoutBottomSheet {
    NONE, ADD, EDIT, TRACK, TRACK_FREE, OPTIONS, TRACKING_OPTIONS, EDIT_TRACK
}