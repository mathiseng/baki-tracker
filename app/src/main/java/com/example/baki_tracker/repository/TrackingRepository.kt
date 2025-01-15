package com.example.baki_tracker.repository

import com.example.baki_tracker.dependencyInjection.Singleton
import com.example.baki_tracker.nutrition.Day
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import me.tatarka.inject.annotations.Inject
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.thread

@Inject
@Singleton
class TrackingRepository : ITrackingRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val client = OkHttpClient()

    private val _trackingState = MutableStateFlow<TrackingState>(TrackingState.Idle)
    override val trackingState: Flow<TrackingState> = _trackingState.asStateFlow()

    suspend fun fetchCurrentDay(): Day {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
        val currentDate = getCurrentDate()

        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("days")
            .document(currentDate)
            .get()
            .await()

        return snapshot.toObject(Day::class.java)
            ?: Day(uuid = 0, date = currentDate, food = emptyList())
    }

    suspend fun fetchHistory(): List<Day> {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")

        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("days")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .await()

        return snapshot.toObjects(Day::class.java)
    }

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date())
    }

    override fun searchFood(query: String) {
        if (query.isBlank()) {
            _trackingState.update { TrackingState.Error("Search query cannot be empty") }
            return
        }

        _trackingState.update { TrackingState.Loading }

        val url = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=$query&search_simple=1&action=process&json=1"
        val request = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "BakiTracker/0.1 (No Website)")
            .build()

        thread {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(response.body?.string() ?: "")
                    val products = jsonResponse.getJSONArray("products")
                    val results = mutableListOf<String>()
                    for (i in 0 until products.length()) {
                        val product = products.getJSONObject(i)
                        val productName = product.optString("product_name", "Unknown Product")
                        results.add(productName)
                    }
                    _trackingState.update { TrackingState.Results(results) }
                } else {
                    _trackingState.update { TrackingState.Error("API Error: ${response.message}") }
                }
            } catch (e: Exception) {
                _trackingState.update { TrackingState.Error("Network Error: ${e.message}") }
            }
        }
    }
}



interface ITrackingRepository {
    val trackingState: Flow<TrackingState>
    fun searchFood(query: String)
}

sealed class TrackingState {
    data object Idle : TrackingState()
    data object Loading : TrackingState()
    data class Results(val results: List<String>) : TrackingState()
    data class Error(val message: String) : TrackingState()
}