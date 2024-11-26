package com.example.baki_tracker.dependencyInjection

import com.example.baki_tracker.workout.manage.ManageWorkoutContainer
import me.tatarka.inject.annotations.Component

/**
 * Here the dependencies and classes we need to inject are placed
 */
@Component
abstract class MainActivityComponent(@Component val parent: ApplicationComponent) {
    //Composables
    abstract val manageWorkoutContainer: ManageWorkoutContainer


}
