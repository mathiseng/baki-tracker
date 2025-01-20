package com.example.baki_tracker.repository

import com.example.baki_tracker.dependencyInjection.Singleton
import com.example.baki_tracker.model.nutrition.FoodItem
import com.example.baki_tracker.model.nutrition.NutritionTrackingDay
import com.example.baki_tracker.utils.formatTimestampToString
import com.example.baki_tracker.utils.getCurrentDateString
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.app
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import me.tatarka.inject.annotations.Inject
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

@Inject
@Singleton
class NutritionRepository : INutritionRepository {
    private val firestore = Firebase.firestore(Firebase.app, "baki-tracker-database")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val _nutritionState = MutableStateFlow<NutritionState>(NutritionState.Idle)
    override val nutritionState: Flow<NutritionState> = _nutritionState.asStateFlow()

    override suspend fun fetchCurrentDay(): NutritionTrackingDay {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
        val currentDate = getCurrentDateString()

        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("days")
            .document(currentDate)
            .get()
            .await()

        return snapshot.toObject(NutritionTrackingDay::class.java)
            ?: NutritionTrackingDay(date = Timestamp.now(), food = emptyList())
    }

    override suspend fun fetchHistory(): List<NutritionTrackingDay> {
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")

        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("days")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .await()

        return snapshot.toObjects(NutritionTrackingDay::class.java)
    }

    override fun fetchProductByBarcode(barcode: String, onSuccess: (FoodItem) -> Unit, onError: (Exception) -> Unit) {
        val url = "https://world.openfoodfacts.org/api/v0/product/$barcode.json"

        val request = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "BakiTracker/0.1 (No Website)")
            .build()

        thread {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(response.body?.string() ?: "")
                    val product = jsonResponse.optJSONObject("product")
                    if (product != null) {
                        val foodItem = FoodItem(
                            uuid = product.optInt("_id", 0).toString(),
                            name = product.optString("product_name", "Unknown Product"),
                            calories = product.optJSONObject("nutriments")?.optDouble("energy-kcal_100g", 0.0)?.toFloat()?.div(100) ?: 0f,
                            protein = product.optJSONObject("nutriments")?.optDouble("proteins_100g", 0.0)?.toFloat()?.div(100) ?: 0f,
                            carbs = product.optJSONObject("nutriments")?.optDouble("carbohydrates_100g", 0.0)?.toFloat()?.div(100) ?: 0f,
                            fat = product.optJSONObject("nutriments")?.optDouble("fat_100g", 0.0)?.toFloat()?.div(100) ?: 0f,
                            quantity =  product.optString("quantity", "1").toFloatOrNull() ?: 1f, // Default quantity for scanned products
                            micronutrients = emptyMap() // Add micronutrients if available
                        )
                        onSuccess(foodItem)
                    } else {
                        onError(Exception("Product not found"))
                    }
                } else {
                    onError(Exception("API Error: ${response.message}"))
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }


    override fun searchFood(query: String) {
        if (query.isBlank()) {
            _nutritionState.update { NutritionState.Error("Search query cannot be empty") }
            return
        }

        _nutritionState.update { NutritionState.Loading }

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
                    val results = mutableListOf<FoodItem>()
                    for (i in 0 until products.length()) {
                        val product = products.getJSONObject(i)
                        //div by 100 because values are given per 100g
                        val productName = product.optString("product_name", "Unknown Product")
                        val productCalories = product.optJSONObject("nutriments")?.optDouble("energy-kcal", 0.0)?.toFloat()?.div(100) ?: 0f
                        val productProtein = product.optJSONObject("nutriments")?.optDouble("proteins_100g", 0.0)?.toFloat()?.div(100) ?: 0f
                        val productCarbs = product.optJSONObject("nutriments")?.optDouble("carbohydrates_100g", 0.0)?.toFloat()?.div(100) ?: 0f
                        val productFat = product.optJSONObject("nutriments")?.optDouble("fat_100g", 0.0)?.toFloat()?.div(100) ?: 0f
                        val productMicronutrients = mutableMapOf<String, Float>()
                        val productQuantity = product.optString("quantity", "1").toFloatOrNull() ?: 1f

                        // Extract micronutrients (optional, depending on API response)
                        val micronutrientKeys = listOf("vitamin_c_100g", "iron_100g", "calcium_100g")
                        micronutrientKeys.forEach { key ->
                            val value = product.optJSONObject("nutriments")?.optDouble(key, 0.0)?.toFloat() ?: 0f
                            if (value > 0) productMicronutrients[key] = value
                        }

                        results.add(
                            FoodItem(
                                uuid = "$i",
                                name = productName,
                                calories = productCalories,
                                protein = productProtein,
                                carbs = productCarbs,
                                fat = productFat,
                                quantity = productQuantity,
                                micronutrients = productMicronutrients
                            )
                        )
                    }
                    _nutritionState.update { NutritionState.Results(results) }
                } else {
                    _nutritionState.update { NutritionState.Error("API Error: ${response.message}") }
                }
            } catch (e: Exception) {
                _nutritionState.update { NutritionState.Error("Network Error: ${e.message}") }
            }
        }
    }
    override fun saveFoodItemToDay(foodItem: FoodItem, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onFailure(Exception("User not authenticated"))
            return
        }

        val userId = currentUser.uid
        val currentDate = Timestamp.now()

        val dayRef = firestore.collection("users")
            .document(userId)
            .collection("days")
            .document(currentDate.formatTimestampToString())

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(dayRef)
            val currentNutritionTrackingDay = if (snapshot.exists()) {
                snapshot.toObject(NutritionTrackingDay::class.java) ?: NutritionTrackingDay(date = currentDate, food = emptyList())
            } else {
                NutritionTrackingDay(date = currentDate, food = emptyList())
            }

            val updatedFoodList = currentNutritionTrackingDay.food.toMutableList().apply {
                add(foodItem)
            }

            val updatedDay = currentNutritionTrackingDay.copy(food = updatedFoodList)
            transaction.set(dayRef, updatedDay)
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }
    override fun observeUserHistory(
        onUpdate: (List<NutritionTrackingDay>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            onError(Exception("User not authenticated"))
            return
        }

        val userId = currentUser.uid
        firestore.collection("users")
            .document(userId)
            .collection("days")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val days = snapshot.toObjects(NutritionTrackingDay::class.java)
                    onUpdate(days)
                }
            }
    }
}



interface INutritionRepository {
    val nutritionState: Flow<NutritionState>
    fun searchFood(query: String)
    fun saveFoodItemToDay(foodItem: FoodItem, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
    fun observeUserHistory(
        onUpdate: (List<NutritionTrackingDay>) -> Unit,
        onError: (Exception) -> Unit
    )
    suspend fun fetchCurrentDay(): NutritionTrackingDay
    suspend fun fetchHistory(): List<NutritionTrackingDay>
    fun fetchProductByBarcode(barcode: String, onSuccess: (FoodItem) -> Unit, onError: (Exception) -> Unit)

}

sealed class NutritionState {
    data object Idle : NutritionState()
    data object Loading : NutritionState()
    data class Results(val results: List<FoodItem>) : NutritionState()
    data class Error(val message: String) : NutritionState()
}