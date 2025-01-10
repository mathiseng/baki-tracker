package com.example.baki_tracker.workout

import com.example.baki_tracker.dependencyInjection.Singleton
import com.example.baki_tracker.model.workout.Workout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tatarka.inject.annotations.Inject

@Inject
@Singleton
class SharedWorkoutStateRepository : ISharedWorkoutStateRepository {
    private val _selectedWorkout: MutableStateFlow<Workout?> = MutableStateFlow(null)
    override val selectedWorkout = _selectedWorkout.asStateFlow()

    private val _selectedBottomSheet: MutableStateFlow<WorkoutBottomSheet> =
        MutableStateFlow(WorkoutBottomSheet.NONE)
    override val selectedBottomSheet = _selectedBottomSheet.asStateFlow()

    override fun updateSelectedWorkout(workout: Workout?) {
        _selectedWorkout.value = workout
    }

    override fun updateSelectedBottomSheet(bottomSheet: WorkoutBottomSheet) {
        _selectedBottomSheet.value = bottomSheet
    }

    override fun dismissBottomSheet() {
        _selectedBottomSheet.value = WorkoutBottomSheet.NONE
    }
}

interface ISharedWorkoutStateRepository {
    val selectedWorkout: StateFlow<Workout?>
    val selectedBottomSheet: StateFlow<WorkoutBottomSheet>

    fun updateSelectedWorkout(workout: Workout?)
    fun updateSelectedBottomSheet(bottomSheet: WorkoutBottomSheet)

    fun dismissBottomSheet()
}

enum class WorkoutBottomSheet {
    NONE, ADD, EDIT, TRACK
}