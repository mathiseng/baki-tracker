package com.example.baki_tracker.dependencyInjection

import com.example.baki_tracker.RootContainer
import com.example.baki_tracker.repository.AuthRepository
import com.example.baki_tracker.repository.IAuthRepository
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

/**
 * Here the dependencies and classes we need to inject are placed
 */
@Component
abstract class MainActivityComponent(@Component val parent: ApplicationComponent) {
    //Composables
    abstract val rootContainer: RootContainer
//    abstract val rootWorkoutContainer: RootWorkoutContainer
//    abstract val rootNutritionContainer: RootNutritionContainer
//    abstract val rootProfileContainer: RootProfileContainer


    //Repositories
    @get:Provides
    val authRepository: IAuthRepository = AuthRepository()

    //Dependecy Provider
   // abstract val dependencyProvider: RootDependencyProvider


}
