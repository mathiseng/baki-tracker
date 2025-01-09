package com.example.baki_tracker.dependencyInjection

import com.example.baki_tracker.workout.workouts.WorkoutsScreen
import com.example.baki_tracker.workout.workouts.WorkoutsViewModel
import com.example.baki_tracker.workout.workouts.manage.ManageWorkoutContainer
import me.tatarka.inject.annotations.Inject

/**
 * Dependency providers should make the code more structured and not overload the UI component parameters with thousands of individual calls.
 */
@Inject
class WorkoutDependencyProvider(
    val workoutsViewModel: () -> WorkoutsViewModel,
    val workoutsScreen: WorkoutsScreen,
    val manageWorkoutContainer: ManageWorkoutContainer
    )