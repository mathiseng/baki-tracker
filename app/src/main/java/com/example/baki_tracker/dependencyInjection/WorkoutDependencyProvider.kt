package com.example.baki_tracker.dependencyInjection

import com.example.baki_tracker.workout.RootWorkoutViewModel
import com.example.baki_tracker.workout.options.OptionsContainer
import com.example.baki_tracker.workout.planning.PlanBottomSheet
import com.example.baki_tracker.workout.planning.PlanningScreen
import com.example.baki_tracker.workout.tracking.TrackingScreen
import com.example.baki_tracker.workout.workouts.WorkoutsScreen
import com.example.baki_tracker.workout.workouts.manage.ManageWorkoutContainer
import me.tatarka.inject.annotations.Inject

/**
 * Dependency providers should make the code more structured and not overload the UI component parameters with thousands of individual calls.
 */
@Inject
class WorkoutDependencyProvider(
    val rootWorkoutViewModel: () -> RootWorkoutViewModel,
    val workoutsScreen: WorkoutsScreen,
    val trackingScreen: TrackingScreen,
    val planningScreen: PlanningScreen,
    val planBottomSheet: PlanBottomSheet,
    val manageWorkoutContainer: ManageWorkoutContainer,
    val optionsContainer: OptionsContainer,
)