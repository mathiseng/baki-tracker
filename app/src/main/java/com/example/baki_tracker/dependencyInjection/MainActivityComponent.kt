package com.example.baki_tracker.dependencyInjection

import com.example.baki_tracker.RootContainer
import com.example.baki_tracker.nutrition.ISharedNutritionStateRepository
import com.example.baki_tracker.nutrition.SharedNutritionStateRepository
import com.example.baki_tracker.repository.AuthRepository
import com.example.baki_tracker.repository.IAuthRepository
import com.example.baki_tracker.repository.INutritionDatabaseRepository
import com.example.baki_tracker.repository.IUserProfileRepository
import com.example.baki_tracker.repository.IWorkoutDatabaseRepository
import com.example.baki_tracker.repository.NutritionDatabaseRepository
import com.example.baki_tracker.repository.UserProfileRepository
import com.example.baki_tracker.repository.WorkoutDatabaseRepository
import com.example.baki_tracker.workout.ISharedWorkoutStateRepository
import com.example.baki_tracker.workout.SharedWorkoutStateRepository
import com.example.baki_tracker.repository.INutritionRepository
import com.example.baki_tracker.repository.NutritionRepository
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
//    abstract val manageWorkoutContainer: ManageWorkoutContainer

    //Repositories
    @get:Provides
    val authRepository: IAuthRepository = AuthRepository()

    @get:Provides
    val nutritionRepository: INutritionRepository = NutritionRepository()

    @get:Provides
    val workoutDatabaseRepository: IWorkoutDatabaseRepository = WorkoutDatabaseRepository()

    @get:Provides
    val nutritionDatabaseRepository: INutritionDatabaseRepository = NutritionDatabaseRepository()

    @get:Provides
    val userProfileRepository: IUserProfileRepository = UserProfileRepository()

    @get:Provides
    val sharedWorkoutStateRepository: ISharedWorkoutStateRepository = SharedWorkoutStateRepository()

    @get:Provides
    val sharedNutritionStateRepository: ISharedNutritionStateRepository = SharedNutritionStateRepository()

    //Dependecy Provider
    // abstract val dependencyProvider: RootDependencyProvider
}
