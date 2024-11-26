package com.example.baki_tracker.workout.manage

import androidx.lifecycle.ViewModel
import com.example.baki_tracker.model.workout.WorkoutExercise
import com.example.baki_tracker.model.workout.WorkoutSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject
import java.util.UUID

/**
 * The ManageWorkoutViewModel should manage events from the UI (e.g. clicks)
 * and should update the uiState accordingly. It should provide the actual uiState as a stateFlow to the UI-components
 */
@Inject
class ManageWorkoutViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ManageWorkoutUiState.initialUiState())
    val uiState = _uiState.asStateFlow()


    fun addExercise() {
        val newExercise = WorkoutExercise(
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
}