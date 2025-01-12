package com.example.baki_tracker.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.example.baki_tracker.dependencyInjection.Singleton
import com.example.baki_tracker.model.workout.Workout
import com.example.baki_tracker.model.workout.WorkoutExercise
import com.example.baki_tracker.model.workout.WorkoutTrackingSession
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
@Singleton
class WorkoutDatabaseRepository() : IWorkoutDatabaseRepository {
    private val db = Firebase.firestore(Firebase.app, "baki-tracker-database")
    private val user = Firebase.auth

    //Path: users/{userId}/workouts/
    private val workoutRef =
        user.currentUser?.let { db.collection("users").document(it.uid).collection("workouts") }

    //Path: users/{userId}/workoutTrackingSessions/
    private val workoutTrackingRef =
        user.currentUser?.let {
            db.collection("users").document(it.uid).collection("workoutTrackingSessions")
        }


    //Path: users/{userId}/exercises/
    private val exerciseRef =
        user.currentUser?.let { db.collection("users").document(it.uid).collection("exercises") }


    private val _exercises: MutableStateFlow<List<WorkoutExercise>> = MutableStateFlow(emptyList())
    override val exercises: StateFlow<List<WorkoutExercise>> = _exercises

    private val _workouts: MutableStateFlow<List<Workout>> = MutableStateFlow(emptyList())
    override val workouts: StateFlow<List<Workout>> = _workouts

    private val _workoutTrackingSessions: MutableStateFlow<List<WorkoutTrackingSession>> =
        MutableStateFlow(emptyList())
    override val workoutTrackingSessions: StateFlow<List<WorkoutTrackingSession>> = _workoutTrackingSessions


    //For predefined Workouts
    override suspend fun getWorkouts() {
        withContext(Dispatchers.IO) {
            workoutRef?.get()?.addOnSuccessListener {
                val workouts = mutableListOf<Workout>()
                for (document in it) {
                    val workout = document.toObject(Workout::class.java)
                    workout.uuid = document.id
                    workouts.add(workout)
                }
                _workouts.update { workouts }
            }
        }
    }

    override suspend fun getExercises() {
        withContext(Dispatchers.IO) {
            val ref = exerciseRef?.get()?.await()
            if (ref != null) {
                val exercises = mutableListOf<WorkoutExercise>()
                for (document in ref) {
                    val exercise = document.toObject(WorkoutExercise::class.java)
                    exercise.uuid = document.id
                    exercises.add(exercise)
                }
                _exercises.update { exercises }
            }
        }
    }

    override suspend fun addWorkout(workout: Workout) {
        withContext(Dispatchers.IO) {
            if (workoutRef != null) {
                val document = workoutRef.document()
                workout.uuid = document.id
                document.set(workout).await()

                _workouts.update { it + workout }
            }
        }
    }

    override suspend fun editWorkout(workout: Workout) {
        withContext(Dispatchers.IO) {
            if (workoutRef != null) {
                val document = workoutRef.document(workout.uuid)
                document.set(workout).await()

                //update the List
                _workouts.update { currentWorkouts ->
                    currentWorkouts.map { workoutItem ->
                        if (workoutItem.uuid == workout.uuid) {
                            //overwrite old workout with updated one
                            workout
                        } else {
                            workoutItem
                        }
                    }
                }
            }
        }
    }

    //For predefined Exercises
    override suspend fun addExercise(exercise: WorkoutExercise) {
        withContext(Dispatchers.IO) {
            if (exerciseRef != null) {
                val document = exerciseRef.document()
                exercise.uuid = document.id
                document.set(exercise).await()

                _exercises.update { it + exercise }
            }
        }
    }

    override suspend fun editExercise(exercise: WorkoutExercise) {
        withContext(Dispatchers.IO) {
            if (exerciseRef != null) {
                val document = exerciseRef.document(exercise.uuid)
                document.set(exercise).await()

                _exercises.update { currentExercises ->
                    currentExercises.map { exerciseItem ->
                        if (exerciseItem.uuid == exercise.uuid) {
                            //overwrite old exercise with updated one
                            exercise
                        } else {
                            exerciseItem
                        }
                    }
                }
            }
        }
    }

    override suspend fun deleteWorkout(uuid: String) {
        withContext(Dispatchers.IO) {
            workoutRef?.document(uuid)?.delete()?.addOnSuccessListener {
                _workouts.update { it.filterNot { it.uuid == uuid } }
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
            }?.addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }
    }

    override suspend fun deleteExercise(uuid: String) {
        withContext(Dispatchers.IO) {
            exerciseRef?.document(uuid)?.delete()?.addOnSuccessListener {
                _exercises.update { it.filterNot { it.uuid == uuid } }
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
            }?.addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }
    }


    // for Tracking Sessions
    override suspend fun addWorkoutTrackingSession(workoutTrackingSession: WorkoutTrackingSession) {
        withContext(Dispatchers.IO) {
            if (workoutTrackingRef != null) {
                val document = workoutTrackingRef.document()
                workoutTrackingSession.uuid = document.id
                document.set(workoutTrackingSession).await()

                _workoutTrackingSessions.update { it + workoutTrackingSession }
            }
        }
    }

    override suspend fun deleteWorkoutTrackingSession(sessionId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun editWorkoutTrackingSession(workoutTrackingSession: WorkoutTrackingSession) {
        TODO("Not yet implemented")
    }
}

interface IWorkoutDatabaseRepository {

    // StateFlow holding the list of WorkoutExercises
    // This will emit updates to the list of exercises.
    val exercises: StateFlow<List<WorkoutExercise>>

    // StateFlow holding the list of Workouts
    // This will emit updates to the list of workouts.
    val workouts: StateFlow<List<Workout>>

    val workoutTrackingSessions: StateFlow<List<WorkoutTrackingSession>>

    /**
     * Retrieves a list of all predefined workouts from the database.
     * This method performs an asynchronous fetch operation to retrieve workout data. Updates the workouts stateFlow you can observe
     */
    suspend fun getWorkouts()

    /**
     * Retrieves a list of all predefined exercises from the database.
     * This method performs an asynchronous fetch operation to retrieve exercise data. Updates the exercises stateFlow you can observe
     */
    suspend fun getExercises()

    /**
     * Adds a new predefined workout to the database.
     * @param workout: The Workout object to be added to the database.
     * This method performs an asynchronous add operation.
     */
    suspend fun addWorkout(workout: Workout)

    /**
     * Edits an existing predefined workout in the database.
     * @param workout: The updated Workout object with the changes to be saved.
     * This method performs an asynchronous update operation.
     */
    suspend fun editWorkout(workout: Workout)

    /**
     * Adds a new predefined exercise to the database.
     * @param exercise: The WorkoutExercise object to be added to the database.
     * This method performs an asynchronous add operation.
     */
    suspend fun addExercise(exercise: WorkoutExercise)

    /**
     * Edits an existing predefined exercise in the database.
     * @param exercise: The updated WorkoutExercise object with the changes to be saved.
     * This method performs an asynchronous update operation.
     */
    suspend fun editExercise(exercise: WorkoutExercise)

    /**
     * Deletes a predefined workout from the database.
     * @param uuid: The unique identifier (UUID) of the workout to be deleted.
     * This method performs an asynchronous delete operation.
     */
    suspend fun deleteWorkout(uuid: String)

    /**
     * Deletes a predefined exercise from the database.
     * @param uuid: The unique identifier (UUID) of the exercise to be deleted.
     * This method performs an asynchronous delete operation.
     */
    suspend fun deleteExercise(uuid: String)

    /**
     * Adds a new workout tracking session to the database.
     * This session will be associated with the user's workout history.
     *
     * @param workoutTrackingSession The session data to be added.
     */
    suspend fun addWorkoutTrackingSession(workoutTrackingSession: WorkoutTrackingSession)

    /**
     * Deletes a specific workout tracking session from the database.
     * This removes the session and its associated data from the user's workout history.
     *
     * @param sessionId The unique ID of the workout tracking session to be deleted.
     */
    suspend fun deleteWorkoutTrackingSession(sessionId: String)

    /**
     * Edits an existing workout tracking session in the database.
     * This updates the details of a specific session.
     *
     * @param workoutTrackingSession The updated workout session data.
     */
    suspend fun editWorkoutTrackingSession(workoutTrackingSession: WorkoutTrackingSession)
}