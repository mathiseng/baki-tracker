package com.example.baki_tracker.repository

import android.util.Log
import com.example.baki_tracker.dependencyInjection.Singleton
import com.example.baki_tracker.model.nutrition.FoodItem
import com.example.baki_tracker.model.nutrition.NutritionTrackingDay
import com.example.baki_tracker.utils.getCurrentDateString
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import me.tatarka.inject.annotations.Inject
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

@Inject
@Singleton
class NutritionRepository : INutritionRepository {
    private val db = Firebase.firestore(Firebase.app, "baki-tracker-database")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val user = Firebase.auth


    private val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).build()

    //Path: users/{userId}/nutritionTrackingDays/
    private val nutritionTrackingRef = user.currentUser?.let {
        db.collection("users").document(it.uid).collection("nutritionTrackingDays")
    }

    private val _nutritionRequestState =
        MutableStateFlow<NutritionRequestState>(NutritionRequestState.Idle)
    override val nutritionRequestState: Flow<NutritionRequestState> =
        _nutritionRequestState.asStateFlow()

    private val _nutritionTrackingDays = MutableStateFlow<List<NutritionTrackingDay>>(emptyList())
    override val nutritionTrackingDays: Flow<List<NutritionTrackingDay>> =
        _nutritionTrackingDays.asStateFlow()


    //Firebase
    override suspend fun getNutritionTrackingDays() {
        val trackingRef = nutritionTrackingRef?.get()?.await()
        var currentDayExists = false
        if (trackingRef != null) {
            val nutritionTrackingDays = mutableListOf<NutritionTrackingDay>()
            for (document in trackingRef) {
                val nutritionTrackingDay = document.toObject(NutritionTrackingDay::class.java)
                //nutritionTrackingDay.uuid = getCurrentDateString()
                if (nutritionTrackingDay.uuid == getCurrentDateString()) {
                    currentDayExists = true
                }
                nutritionTrackingDays.add(nutritionTrackingDay)
            }
            if (!currentDayExists) nutritionTrackingDays.add(NutritionTrackingDay())
            _nutritionTrackingDays.update { nutritionTrackingDays }
        }
    }

    override suspend fun addFoodItemToTrackingDay(
        foodItem: FoodItem, trackingDayId: String
    ) {
        if (nutritionTrackingRef != null) {
            val trackingDayDoc = nutritionTrackingRef.document(trackingDayId)

            db.runTransaction { transaction ->
                val snapshot = transaction.get(trackingDayDoc)
                val currentNutritionTrackingDay = if (snapshot.exists()) {
                    snapshot.toObject(NutritionTrackingDay::class.java) ?: NutritionTrackingDay()
                } else {
                    NutritionTrackingDay()
                }

                val updatedFoodList = currentNutritionTrackingDay.foodItems.toMutableList().apply {
                    add(foodItem)
                }

                val updatedDay = currentNutritionTrackingDay.copy(foodItems = updatedFoodList)
                transaction.set(trackingDayDoc, updatedDay)

                _nutritionTrackingDays.value = _nutritionTrackingDays.value.map {
                    if (it.uuid == trackingDayId) updatedDay else it
                }
            }
        }
    }

    override suspend fun deleteFoodItemFromTrackingDay(
        foodItemId: String, trackingDayId: String
    ) {
        val trackingDay = _nutritionTrackingDays.value.firstOrNull { it.uuid == trackingDayId }
        val updatedList = trackingDay?.foodItems?.filterNot { it.uuid == foodItemId } ?: emptyList()

        if (nutritionTrackingRef != null) {
            val trackingDayDoc = nutritionTrackingRef.document(trackingDayId)
            trackingDayDoc.update("foodItems", updatedList).await()

            _nutritionTrackingDays.value = _nutritionTrackingDays.value.map {
                if (it.uuid == trackingDayId) it.copy(foodItems = updatedList) else it
            }
            Log.d("NutritionRepo", "DocumentSnapshot successfully deleted!")
        }
    }

    override suspend fun editFoodItemFromTrackingDay(
        foodItem: FoodItem, trackingDayId: String
    ) {
        val trackingDay = _nutritionTrackingDays.value.firstOrNull { it.uuid == trackingDayId }
        val updatedList =
            trackingDay?.foodItems?.map { if (it.uuid == foodItem.uuid) foodItem else it }
                ?: emptyList()

        if (nutritionTrackingRef != null) {
            val trackingDayDoc = nutritionTrackingRef.document(trackingDayId)
            trackingDayDoc.update("foodItems", updatedList).await()

            _nutritionTrackingDays.value = _nutritionTrackingDays.value.map {
                if (it.uuid == trackingDayId) it.copy(foodItems = updatedList) else it
            }
        }
    }

    //====================//
    //OpenFoodFacts logic
    override fun fetchProductByBarcode(barcode: String) {
        val url = "https://world.openfoodfacts.org/api/v0/product/$barcode.json"

        val request =
            Request.Builder().url(url).addHeader("User-Agent", "BakiTracker/0.1 (No Website)")
                .build()

        thread {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(response.body?.string() ?: "")
                    val product = jsonResponse.optJSONObject("product")
                    if (product != null) {
                        val foodItem = deserializeProduct(product)
                        _nutritionRequestState.update { NutritionRequestState.SingleResult(foodItem) }
                    } else {
                        _nutritionRequestState.update { NutritionRequestState.Error("Product not found") }
                    }
                } else {
                    _nutritionRequestState.update { NutritionRequestState.Error("API Error: ${response.message}") }
                }
            } catch (e: Exception) {
                _nutritionRequestState.update { NutritionRequestState.Error("${e.message}") }

            }
        }
    }

/*
    override fun searchFood(query: String) {
        if (query.isBlank()) {
            _nutritionRequestState.update { NutritionRequestState.Error("Search query cannot be empty") }
            return
        }

        _nutritionRequestState.update { NutritionRequestState.Loading }



        val url =
            "https://world.openfoodfacts.org/cgi/search.pl?search_terms=$query&search_simple=1&action=process&json=1"
        val request =
            Request.Builder().url(url).addHeader("User-Agent", "BakiTracker/0.1 (No Website)")
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
                        val foodItem =deserializeProduct(product)

                        results.add(foodItem)
                    }
                    _nutritionRequestState.update { NutritionRequestState.Results(results) }
                } else {
                    _nutritionRequestState.update { NutritionRequestState.Error("API Error: ${response.message}") }
                }
            } catch (e: Exception) {
                _nutritionRequestState.update { NutritionRequestState.Error("Network Error: ${e.message}") }
            }
        }
    }*/

    private fun deserializeProduct(product: JSONObject): FoodItem {
        //div by 100 because values are given per 100g
        val productName = product.optString("product_name", "Unknown Product")
        val productCalories =
            product.optJSONObject("nutriments")?.optDouble("energy-kcal", 0.0)
                ?.toFloat()?.div(100) ?: 0f
        val productProtein =
            product.optJSONObject("nutriments")?.optDouble("proteins_100g", 0.0)
                ?.toFloat()?.div(100) ?: 0f
        val productCarbs = product.optJSONObject("nutriments")
            ?.optDouble("carbohydrates_100g", 0.0)?.toFloat()?.div(100) ?: 0f
        val productFat =
            product.optJSONObject("nutriments")?.optDouble("fat_100g", 0.0)
                ?.toFloat()?.div(100) ?: 0f
        val productMicronutrients = mutableMapOf<String, Float>()
        val productQuantity =
            product.optString("quantity", "1").toFloatOrNull() ?: 1f

        // Extract micronutrients (optional, depending on API response)
        val micronutrientKeys =
            listOf("vitamin_c_100g", "iron_100g", "calcium_100g")
        micronutrientKeys.forEach { key ->
            val value =
                product.optJSONObject("nutriments")?.optDouble(key, 0.0)?.toFloat()
                    ?: 0f
            if (value > 0) productMicronutrients[key] = value
        }

        return FoodItem(
            name = productName,
            calories = productCalories,
            protein = productProtein,
            carbs = productCarbs,
            fat = productFat,
            quantity = productQuantity,
            micronutrients = productMicronutrients
        )
    }


    //USDA Logic
    override fun searchFood(query: String) {
        if (query.isBlank()) {
            _nutritionRequestState.update { NutritionRequestState.Error("Search query cannot be empty") }
            return
        }

        _nutritionRequestState.update { NutritionRequestState.Loading }

        // USDA API key
        val apiKey = "noiZQKV79NVmuqWW4g1biJuR6U45D3JtrXzDTf3V" // Replace with your actual API key

        val cleanedQuery = query.trim().lowercase().replace(Regex("[^a-z ]"), "")
        val url = "https://api.nal.usda.gov/fdc/v1/foods/search?query=$cleanedQuery&api_key=$apiKey"
        val request = Request.Builder()
            .url(url)
            .build()

        thread {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(response.body?.string() ?: "")
                    val foods = jsonResponse.getJSONArray("foods")
                    val results = mutableListOf<FoodItem>()

                    for (i in 0 until foods.length()) {
                        val product = foods.getJSONObject(i)
                        val foodItem = deserializeProductUSDA(product)
                        if (foodItem != null) {
                            results.add(foodItem)
                        }
                    }
                    _nutritionRequestState.update { NutritionRequestState.Results(results) }
                } else {
                    _nutritionRequestState.update { NutritionRequestState.Error("API Error: ${response.message}") }
                }
            } catch (e: Exception) {
                _nutritionRequestState.update { NutritionRequestState.Error("Network Error: ${e.message}") }
            }
        }
    }
    private fun JSONArray.findNutrient(nutrientName: String): Double? {
        for (i in 0 until this.length()) {
            val nutrient = this.getJSONObject(i)
            if (nutrient.optString("nutrientName") == nutrientName) {
                return nutrient.optDouble("value", 0.0)
            }
        }
        return null
    }

    private fun calculateMatchScore(query: String, name: String): Int {
        val cleanedQuery = query.lowercase()
        val cleanedName = name.lowercase()

        return when {
            cleanedName == cleanedQuery -> 100 // Exact match
            cleanedName.startsWith(cleanedQuery) -> 75 // Starts with the query
            cleanedName.contains(cleanedQuery) -> 50 // Contains the query
            else -> 0 // No match
        }
    }


    private fun deserializeProductUSDA(product: JSONObject): FoodItem? {
        val productName = product.optString("description", "Unknown Product")
        // Skip results where the name is in all caps
        if (productName == productName.uppercase()) {
            return null // Skip this product
        }

        val productCalories = product.optJSONArray("foodNutrients")
            ?.findNutrient("Energy")
            ?.toFloat()
            ?.div(100) // Already for 100g in USDA, no division needed
            ?: 0f
        val productProtein = product.optJSONArray("foodNutrients")
            ?.findNutrient("Protein")
            ?.toFloat()
            ?.div(100) // Divide by 1 for clarity
            ?: 0f
        val productCarbs = product.optJSONArray("foodNutrients")
            ?.findNutrient("Carbohydrate, by difference")
            ?.toFloat()
            ?.div(100) ?: 0f
        val productFat = product.optJSONArray("foodNutrients")
            ?.findNutrient("Total lipid (fat)")
            ?.toFloat()
            ?.div(100) ?: 0f

        val productMicronutrients = mutableMapOf<String, Float>()
        val micronutrientKeys = listOf("Vitamin C", "Iron", "Calcium")
        micronutrientKeys.forEach { key ->
            val value = product.optJSONArray("foodNutrients")
                ?.findNutrient(key)
                ?.toFloat()
                ?: 0f
            if (value > 0) productMicronutrients[key] = value
        }

        return FoodItem(
            uuid = product.optString("fdcId", "0"),
            name = productName,
            calories = productCalories, // Per Gram
            protein = productProtein,
            carbs = productCarbs,
            fat = productFat,
            quantity = 1f, // All Values are per 1g
            micronutrients = productMicronutrients
        )
    }




}


interface INutritionRepository {
    val nutritionRequestState: Flow<NutritionRequestState>
    val nutritionTrackingDays: Flow<List<NutritionTrackingDay>>

    fun searchFood(query: String)

    fun fetchProductByBarcode(barcode: String)

    /**
     * Retrieves a list of all nutrition tracking days. Updates the nutritionTrackingDays stateFlow you can observe
     */
    suspend fun getNutritionTrackingDays()

    /**
     * Adds a nutrition entry to an existing nutrition tracking day. If the day does not exist, create it.
     * @param foodItem: The entry to add (e.g., a food item for the day).
     * @param trackingDayId: The ID of the tracking day where the entry should be added.
     */
    suspend fun addFoodItemToTrackingDay(
        foodItem: FoodItem, trackingDayId: String
    )

    /**
     * Deletes a specific nutrition entry from a given nutrition tracking day.
     * @param foodItemId: The ID of the nutrition entry to be deleted.
     * @param trackingDayId: The ID of the tracking day where the entry should be removed.
     */
    suspend fun deleteFoodItemFromTrackingDay(foodItemId: String, trackingDayId: String)

    /**
     * Edits an existing nutrition entry within a nutrition tracking day.
     * @param foodItem: The updated nutrition entry to replace the existing one.
     * @param trackingDayId: The ID of the tracking day containing the entry to edit.
     */
    suspend fun editFoodItemFromTrackingDay(
        foodItem: FoodItem, trackingDayId: String
    )
}

sealed class NutritionRequestState {
    data object Idle : NutritionRequestState()
    data object Loading : NutritionRequestState()
    data class Results(val results: List<FoodItem>) : NutritionRequestState()
    data class SingleResult(val foodItem: FoodItem) :
        NutritionRequestState() // For a product which was scanned by bar code

    data class Error(val message: String) : NutritionRequestState()
}