package com.example.baki_tracker.workout.workouts.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baki_tracker.model.workout.TrackedWorkoutExercise
import com.example.baki_tracker.model.workout.Workout
import com.example.baki_tracker.model.workout.WorkoutExercise
import com.example.baki_tracker.model.workout.WorkoutSet
import com.example.baki_tracker.model.workout.WorkoutTrackingSession
import com.example.baki_tracker.model.workout.WorkoutType
import com.example.baki_tracker.repository.IWorkoutDatabaseRepository
import com.example.baki_tracker.workout.ISharedWorkoutStateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import java.util.UUID

/**
 * The ManageWorkoutViewModel should manage events from the UI (e.g. clicks)
 * and should update the uiState accordingly. It should provide the actual uiState as a stateFlow to the UI-components
 */
@Inject
class ManageWorkoutViewModel(
    val workoutDatabaseRepository: IWorkoutDatabaseRepository,
    val sharedWorkoutStateRepository: ISharedWorkoutStateRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ManageWorkoutUiState.initialUiState())
    val uiState = _uiState.asStateFlow()

    private var session: WorkoutTrackingSession? = null

    init {
        viewModelScope.launch {
            sharedWorkoutStateRepository.selectedWorkout.collect { workout ->
                if (workout != null) {
                    _uiState.update {
                        it.copy(
                            workout = workout,
                            workoutName = workout.name,
                            workoutType = workout.workoutType,
                            exercises = workout.exercises.map { exercise -> WorkoutExerciseUi(exercise.uuid,exercise.name,exercise.sets) },
                        )
                    }
                } else {
                    _uiState.update {
                        ManageWorkoutUiState.initialUiState()
                    }
                }
            }
        }

        viewModelScope.launch {
            sharedWorkoutStateRepository.selectedWorkoutTrackingSession.collect { session ->
                if (session != null) {
                    this@ManageWorkoutViewModel.session = session
                    _uiState.update {
                        it.copy(
                            workoutName = session.name,
                            exercises = session.trackedExercises.map { exercise -> WorkoutExerciseUi(exercise.uuid,exercise.name,exercise.sets) },
                        )
                    }
                } else {
                    _uiState.update {
                        this@ManageWorkoutViewModel.session = null
                        ManageWorkoutUiState.initialUiState()
                    }
                }
            }
        }
    }

    fun addExercise() {
        val newExercise = WorkoutExerciseUi(
            uuid = UUID.randomUUID().toString(), name = "", sets = listOf() // Start with no sets
        )
        _uiState.update { currentState ->
            currentState.copy(exercises = currentState.exercises + newExercise)
        }
    }

    fun onExerciseNameChange(exerciseId: String, newName: String) {
        _uiState.update { currentState ->
            val updatedExercises = currentState.exercises.map { exercise ->
                if (exercise.uuid == exerciseId) {
                    exercise.copy(name = newName)
                } else exercise
            }

            currentState.copy(exercises = updatedExercises)
        }
    }

    fun deleteExercise(exerciseId: String) {
        _uiState.update { currentState ->
            currentState.copy(exercises = currentState.exercises.filterNot { it.uuid == exerciseId })
        }
    }

    fun addSetToExercise(exerciseId: String) {
        _uiState.update { currentState ->
            val updatedExercises = currentState.exercises.map { exercise ->
                if (exercise.uuid == exerciseId) {
                    val newSet = WorkoutSet(
                        uuid = UUID.randomUUID().toString(), // Increment set number
                        reps = 0, // Default reps
                        weight = 0.0 // Default weight
                    )
                    exercise.copy(sets = exercise.sets + newSet)
                } else exercise
            }
            currentState.copy(exercises = updatedExercises)
        }
    }

    // Delete a set from a specific exercise
    fun deleteSetFromExercise(exerciseId: String, setId: String) {
        _uiState.update { currentState ->
            val updatedExercises = currentState.exercises.map { exercise ->
                if (exercise.uuid == exerciseId) {
                    exercise.copy(sets = exercise.sets.filterNot { it.uuid == setId })
                } else exercise
            }
            currentState.copy(exercises = updatedExercises)
        }
    }

    fun onSetChange(exerciseId: String, setId: String, newReps: Int, newWeight: Double) {
        val updatedExercises = uiState.value.exercises.map { exercise ->
            if (exercise.uuid == exerciseId) {
                exercise.copy(sets = exercise.sets.map { set ->
                    if (set.uuid == setId) set.copy(reps = newReps, weight = newWeight)
                    else set
                })
            } else exercise
        }
        _uiState.value = uiState.value.copy(exercises = updatedExercises)
    }

    fun onWorkoutNameChange(name: String) {
        _uiState.update { it.copy(workoutName = name) }
    }

    fun onWorkoutTypeChange(type: WorkoutType) {
        _uiState.update { it.copy(workoutType = type) }
    }

    fun onSaveWorkout(manageWorkoutMode: ManageWorkoutMode) {
        viewModelScope.launch {
            try {
                if (manageWorkoutMode == ManageWorkoutMode.CREATE) {
                    val workout = Workout(
                        name = uiState.value.workoutName, exercises = uiState.value.exercises.map { WorkoutExercise(it.uuid,it.name,it.sets) }
                    )

                    uiState.value.workoutType?.let {
                        workout.type = it.toMap()
                    }

                    workoutDatabaseRepository.addWorkout(workout)
                } else if (manageWorkoutMode == ManageWorkoutMode.EDIT) {
                    val workout = sharedWorkoutStateRepository.selectedWorkout.value?.copy(
                        name = uiState.value.workoutName, exercises =  uiState.value.exercises.map { WorkoutExercise(it.uuid,it.name,it.sets) }
                    )
                    if (workout != null) {
                        uiState.value.workoutType?.let {
                            workout.type = it.toMap()
                        }
                        workoutDatabaseRepository.editWorkout(workout)
                    }
                } else if(manageWorkoutMode == ManageWorkoutMode.EDIT_TRACK) {
                    val trackedWorkoutExercises = uiState.value.exercises.map {
                        TrackedWorkoutExercise(it.uuid,
                            name = it.name,
                            sets = it.sets.map {
                                WorkoutSet(
                                    reps = it.reps, weight = it.weight
                                )
                            })
                    }

                    session?.let {
                        workoutDatabaseRepository.editWorkoutTrackingSession(
                            it.copy(
                                name = uiState.value.workoutName,
                                trackedExercises = trackedWorkoutExercises
                            )
                        )
                    }

                }else {
                    val predefinedWorkout = uiState.value.workout
                    val uuid = predefinedWorkout?.uuid ?: ""
                    val trackedWorkoutExercises = uiState.value.exercises.map {
                        TrackedWorkoutExercise(uuid = it.uuid,
                            name = it.name,
                            sets = it.sets.map {
                                WorkoutSet(
                                    reps = it.reps, weight = it.weight
                                )
                            })
                    }

                    workoutDatabaseRepository.addWorkoutTrackingSession(
                        WorkoutTrackingSession(
                            workoutId = uuid,
                            name = uiState.value.workoutName,
                            trackedExercises = trackedWorkoutExercises
                        )
                    )
                }
                onDismiss()
            } catch (_: Exception) {
            }
        }
    }

    fun onDismiss() {
        _uiState.update { ManageWorkoutUiState.initialUiState() }
        sharedWorkoutStateRepository.dismissBottomSheet()
        sharedWorkoutStateRepository.updateSelectedWorkout(null)
    }
}