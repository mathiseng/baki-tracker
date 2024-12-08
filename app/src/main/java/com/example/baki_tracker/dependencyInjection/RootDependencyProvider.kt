package com.example.baki_tracker.dependencyInjection

import com.example.baki_tracker.auth.AuthScreen
import com.example.baki_tracker.auth.AuthViewModel
import com.example.baki_tracker.nutrition.RootNutritionContainer
import com.example.baki_tracker.profile.RootProfileContainer
import com.example.baki_tracker.workout.RootWorkoutContainer
import me.tatarka.inject.annotations.Inject

/**
 * Dependency providers should make the code more structured and not overload the UI component parameters with thousands of individual calls.
 */
@Inject
class RootDependencyProvider(
    val authViewModel: () -> AuthViewModel,
    val authScreen: AuthScreen,
    val rootWorkoutContainer: RootWorkoutContainer,
    val rootNutritionContainer: RootNutritionContainer,
    val rootProfileContainer: RootProfileContainer
)