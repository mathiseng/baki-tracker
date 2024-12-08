package com.example.baki_tracker.repository

import android.util.Log
import com.example.baki_tracker.dependencyInjection.Singleton
import com.example.baki_tracker.model.nutrition.NutritionEntry
import com.example.baki_tracker.model.nutrition.NutritionTrackingDay
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import me.tatarka.inject.annotations.Inject

@Inject
@Singleton
class NutritionDatabaseRepository() : INutritionDatabaseRepository {
    private val db = Firebase.firestore(Firebase.app, "baki-tracker-database")
    private val user = Firebase.auth

    //Path: users/{userId}/nutritionTrackingDays/
    private val nutritionTrackingRef = user.currentUser?.let {
        db.collection("users").document(it.uid).collection("nutritionTrackingDays")
    }


    private val _nutritionTrackingDays: MutableStateFlow<List<NutritionTrackingDay>> =
        MutableStateFlow(emptyList())
    override val nutritionTrackingDays: StateFlow<List<NutritionTrackingDay>> =
        _nutritionTrackingDays

    override suspend fun getNutritionTrackingDays() {
        val trackingRef = nutritionTrackingRef?.get()?.await()
        if (trackingRef != null) {
            val nutritionTrackingDays = mutableListOf<NutritionTrackingDay>()
            for (document in trackingRef) {
                val nutritionTrackingDay = document.toObject(NutritionTrackingDay::class.java)
                nutritionTrackingDay.uuid = document.id
                nutritionTrackingDays.add(nutritionTrackingDay)
            }
            _nutritionTrackingDays.update { nutritionTrackingDays }
        }
    }

    override suspend fun addNutritionEntryToTrackingDay(
        nutritionEntry: NutritionEntry, trackingDayId: String
    ) {
        val currentTrackingDay =
            _nutritionTrackingDays.value.firstOrNull { it.uuid == trackingDayId }
        val updatedList = (currentTrackingDay?.nutritionEntries ?: emptyList()) + nutritionEntry

        if (nutritionTrackingRef != null) {
            val trackingDayDoc = nutritionTrackingRef.document(trackingDayId)

            val document = trackingDayDoc.get().await()
            if (!document.exists()) {
                //create a new document and update value with NutritionTrackingDay
                val trackingDocument = nutritionTrackingRef.document()
                val updatedTrackingDay = NutritionTrackingDay(
                    trackingDocument.id, Timestamp.now(), updatedList
                )
                trackingDocument.set(updatedTrackingDay).await()
            } else {
                //if already exists then update the field of the doc
                trackingDayDoc.update("nutritionEntries", updatedList).await()
            }

        }
    }

    override suspend fun deleteNutritionEntryFromTrackingDay(
        nutritionEntryId: String, trackingDayId: String
    ) {
        val trackingDay = _nutritionTrackingDays.value.firstOrNull { it.uuid == trackingDayId }
        val updatedList =
            trackingDay?.nutritionEntries?.filterNot { it.uuid == nutritionEntryId } ?: emptyList()

        if (nutritionTrackingRef != null) {
            val trackingDayDoc = nutritionTrackingRef.document(trackingDayId)
            trackingDayDoc.update("nutritionEntries", updatedList).await()
            Log.d("NutritionRepo", "DocumentSnapshot successfully deleted!")
        }
    }

    override suspend fun editNutritionEntryFromTrackingDay(
        nutritionEntry: NutritionEntry, trackingDayId: String
    ) {
        val trackingDay = _nutritionTrackingDays.value.firstOrNull { it.uuid == trackingDayId }
        val updatedList =
            trackingDay?.nutritionEntries?.map { if (it.uuid == nutritionEntry.uuid) nutritionEntry else it }
                ?: emptyList()

        if (nutritionTrackingRef != null) {
            val trackingDayDoc = nutritionTrackingRef.document(trackingDayId)
            trackingDayDoc.update("nutritionEntries", updatedList).await()
        }
    }
}

interface INutritionDatabaseRepository {

    // StateFlow holding the list of NutritionTrackingDays
    // This will emit updates to the list of nutritionTrackingDays.
    val nutritionTrackingDays: StateFlow<List<NutritionTrackingDay>>

    /**
     * Retrieves a list of all nutrition tracking days. Updates the nutritionTrackingDays stateFlow you can observe
     */
    suspend fun getNutritionTrackingDays()

    /**
     * Adds a nutrition entry to an existing nutrition tracking day. If the day does not exist, create it.
     * @param nutritionEntry: The entry to add (e.g., a food item for the day).
     * @param trackingDayId: The ID of the tracking day where the entry should be added.
     */
    suspend fun addNutritionEntryToTrackingDay(
        nutritionEntry: NutritionEntry, trackingDayId: String
    )

    /**
     * Deletes a specific nutrition entry from a given nutrition tracking day.
     * @param nutritionEntryId: The ID of the nutrition entry to be deleted.
     * @param trackingDayId: The ID of the tracking day where the entry should be removed.
     */
    suspend fun deleteNutritionEntryFromTrackingDay(nutritionEntryId: String, trackingDayId: String)

    /**
     * Edits an existing nutrition entry within a nutrition tracking day.
     * @param nutritionEntry: The updated nutrition entry to replace the existing one.
     * @param trackingDayId: The ID of the tracking day containing the entry to edit.
     */
    suspend fun editNutritionEntryFromTrackingDay(
        nutritionEntry: NutritionEntry, trackingDayId: String
    )
}