package com.example.baki_tracker.repository

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.baki_tracker.dependencyInjection.Singleton
import com.example.baki_tracker.model.workout.PlannedWorkout
import com.example.baki_tracker.model.workout.Workout
import com.example.baki_tracker.utils.formatUtcToLocalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private val storageName = "com.example.baki_tracker" + "_preferences"

//Define Datastore and save Credentials and calendarId in DataStore
private val Context.dataStore by
preferencesDataStore(
    name = storageName,
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, storageName))
    },
)

@Inject
@Singleton
class GoogleRepository(val context: Context) : IGoogleRepository {

    private val _authState = MutableStateFlow<GoogleAuthState>(GoogleAuthState.Unauthenticated)
    override var authState: Flow<GoogleAuthState> = _authState.asStateFlow()
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _authorizationRequest: MutableStateFlow<AuthorizationRequest?> =
        MutableStateFlow(null)
    override val authorizationRequest: Flow<AuthorizationRequest?> = _authorizationRequest

    private val _plannedWorkouts: MutableStateFlow<List<PlannedWorkout>> =
        MutableStateFlow(emptyList())
    override val plannedWorkouts: Flow<List<PlannedWorkout>> = _plannedWorkouts

    private var googleAuthState: AuthState = AuthState(AppAuth.serviceConfiguration)

    private val AUTHSTATE_KEY = stringPreferencesKey("authState")
    private val CALENDAR_KEY = stringPreferencesKey("calendarId")

    val authService = AuthorizationService(context)
    //val authService = AuthorizationService(context)

    init {
        repositoryScope.launch {
            restoreState()
        }
    }


    //============== API CALLS ================================================

    override suspend fun getCalendarEvents() {
        val calendarId = getWorkoutCalendarId()
        val url = " https://www.googleapis.com/calendar/v3/calendars/${calendarId}/events"

        withContext(Dispatchers.IO) {
            val response = makeApiRequest(url, "GET")
            if (response != null && response.isSuccessful) {
                val responseBody = response.body?.string()
                val events = JSONObject(responseBody).getJSONArray("items")
                val list = mutableListOf<PlannedWorkout>()
                for (i in 0 until events.length()) {
                    val event = events.getJSONObject(i)
                    val plannedWorkout = deserializeEventToPlannedWorkout(event, calendarId)
                    list.add(plannedWorkout)

                    //HERE I HAVE TO TRANSFORM THE EVENTS INTO PLANNED WORKOUTS
                }
                _plannedWorkouts.update { list }
                Log.d("TESTOO RESPONSE EVENTS", "$responseBody")
            } else {
                // throw Exception("Failed to fetch events: ${response.code}")
            }
        }

    }

    override suspend fun planCalendarEvent(startTime: String, endTime: String, workout: Workout) {
        val calendarId = getWorkoutCalendarId()
        val url = "https://www.googleapis.com/calendar/v3/calendars/${calendarId}/events"

        val jsonBody = getEventJson(startTime, endTime, workout)

        withContext(Dispatchers.IO) {
            val response = makeApiRequest(url, "POST", jsonBody)
            if (response != null && response.isSuccessful) {
                Log.d("TESTII RESP", response.message)
                getCalendarEvents()
            }
        }
    }

    override suspend fun deleteCalendarEvent(eventId: String) {
        val calendarId = getWorkoutCalendarId()

        val url = "https://www.googleapis.com/calendar/v3/calendars/${calendarId}/events/${eventId}"

        withContext(Dispatchers.IO) {
            val response = makeApiRequest(url, "DELETE")

            if (response != null && response.isSuccessful) {
                getCalendarEvents()
            }
        }
    }

    override suspend fun updateCalendarEvent(
        eventId: String,
        startTime: String,
        endTime: String,
        workout: Workout
    ) {
        val calendarId = getWorkoutCalendarId()
        val url = "https://www.googleapis.com/calendar/v3/calendars/${calendarId}/events/${eventId}"

        val jsonBody = getEventJson(startTime, endTime, workout)
        withContext(Dispatchers.IO) {
            val response = makeApiRequest(url, "PATCH", jsonBody)

            if (response != null && response.isSuccessful) {
                getCalendarEvents()
            }
        }
    }


    //============= Helper Functions for API Calls =========================

    private fun getEventJson(startTime: String, endTime: String, workout: Workout): String {
        return "{\n" +
                "  \"end\": {\n" +
                "    \"dateTime\": \"${endTime}\",\n" +
                "    \"timeZone\": \"Europe/Berlin\"\n" +
                "    \n" +
                "  },\n" +
                "  \"start\": {\n" +
                "    \"dateTime\": \"${startTime}\",\n" +
                "    \"timeZone\": \"Europe/Berlin\"\n" +
                "    \n" +
                "  },\n" +
                "  \"summary\": \"${workout.name}\",\n" +
                "  \"description\": \"${workout.exercises.size} Exercises\",\n" +
                "  \"extendedProperties\": {\n" +
                "    \"private\": {\n" +
                "      \"workoutId\": \"${workout.uuid}\"\n" +
                "      \n" +
                "    }\n" +
                "    \n" +
                "  }\n" +
                "  \n" +
                "}"
    }

    @SuppressLint("NewApi")
    private fun deserializeEventToPlannedWorkout(
        event: JSONObject,
        calendarId: String
    ): PlannedWorkout {
        val workoutId = event.getJSONObject("extendedProperties").getJSONObject("private")
            .getString("workoutId")
        val eventId = event.getString("id")
        val title = event.getString("summary")
        val startTime = formatUtcToLocalTime(event.getJSONObject("start").getString("dateTime"))
        val endTime = formatUtcToLocalTime(event.getJSONObject("end").getString("dateTime"))
        val date =
            formatUtcToLocalTime(event.getJSONObject("start").getString("dateTime"), "dd.MM.yyyy")
        val description = event.getString("description")

        return PlannedWorkout(
            eventId = eventId,
            workoutId = workoutId,
            title = title,
            date = date,
            startTime = startTime,
            endTime = endTime,
            calendarId = calendarId,
            description = description
        )
    }

    private suspend fun getWorkoutCalendarId(): String {
        val savedCalendarId = context.dataStore.data.first()[CALENDAR_KEY]
        Log.d("TESTOO SAVED CALENDAR", "$savedCalendarId")
        if (!savedCalendarId.isNullOrEmpty()) return savedCalendarId

        //Here i have to fetch all calendars and search for summary workouts...if exists return id of this calendar
        val url = "https://www.googleapis.com/calendar/v3/users/me/calendarList"
        val response = makeApiRequest(url, "GET")
        Log.d("TESTOO RESPONSE OF CALENDAR", "$response")

        if (response != null && response.isSuccessful) {
            val responseBody = response.body?.string()
            val calendars = JSONObject(responseBody).getJSONArray("items")
            for (i in 0 until calendars.length()) {
                val calendar = calendars.getJSONObject(i)
                if (calendar.getString("summary") == "Workouts") {
                    saveCalendarId(calendar.getString("id"))
                    return calendar.getString("id")
                }
            }
            // If not found, create a new calendar
            return createWorkoutCalendar()
        } else {
            throw Exception("Failed to fetch calendar list: ${response?.code}")
        }
        //Here i have to fetch all calendars and search for summary workouts...if exists return id of this calendar

        //if calendar also not exist remote create a new calendar
    }

    private suspend fun createWorkoutCalendar(): String {
        val url = "https://www.googleapis.com/calendar/v3/calendars"
        val body = JSONObject().apply {
            put("summary", "Workouts")
            put("timeZone", "UTC")
        }.toString()

        val response = makeApiRequest(url, "POST", body)
        if (response != null && response.isSuccessful) {
            val responseBody = response.body?.string()
            val calendarId = JSONObject(responseBody).getString("id")
            saveCalendarId(calendarId)
            return calendarId
        } else {
            throw Exception("Failed to create calendar: ${response?.code}")
        }
    }


    private suspend fun makeApiRequest(
        url: String,
        method: String,
        body: String? = null
    ): Response? {
        return withContext(Dispatchers.IO) {
            try {

                val client = OkHttpClient()
                val accessToken = refreshAccessTokenIfNeeded()

                val requestBuilder = Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer $accessToken")

                if (method == "POST") {
                    val requestBody = body?.toRequestBody("application/json".toMediaType())
                    requestBuilder.post(requestBody!!)
                } else if (method == "PATCH") {
                    val requestBody = body?.toRequestBody("application/json".toMediaType())
                    requestBuilder.patch(requestBody!!)
                } else if (method == "DELETE") {
                    requestBuilder.delete()
                }

                val request = requestBuilder.build()
                return@withContext client.newCall(request).execute()


            } catch (_: Exception) {
                return@withContext null
            }
        }
    }

    private suspend fun refreshAccessTokenIfNeeded(): String {
        return suspendCoroutine { continuation ->
            Log.d("TESTII OLD TOKEN", "${googleAuthState.accessToken}")

            if (googleAuthState.needsTokenRefresh) {
                Log.d(
                    "TOKEN_REFRESH",
                    "Token needs refresh. Attempting to refresh...${googleAuthState.authorizationServiceConfiguration!!.toJson()}"
                )

                googleAuthState.refreshToken?.let {

                    val tokenRequest = AppAuth.getRefreshTokenRequest(it)
                    authService.performTokenRequest(tokenRequest) { response, exception ->

                        Log.e("TESTII RESULT REFRESH", "Token ${response?.accessToken} Exception ${exception}")

                        if (exception != null) {
                            Log.e("TOKEN_REFRESH", "Failed to refresh token: ${exception.localizedMessage}")
                            continuation.resumeWith(
                                Result.failure(
                                    Exception(
                                        "Failed to refresh token",
                                        exception
                                    )
                                )
                            )
                        } else if (response?.accessToken != null) {
                            Log.d("TOKEN_REFRESH", "Token refreshed successfully: ${response.accessToken}")

                            // Save the updated auth state to DataStore
                            repositoryScope.launch {
                                googleAuthState.update(response,null)
                                saveTokenResult(googleAuthState)
                            }

                            // Resume the coroutine with the fresh access token
                            continuation.resume(response.accessToken!!)
                        } else {
                            Log.e("TOKEN_REFRESH", "Access token is null after refresh attempt.")
                            continuation.resumeWith(Result.failure(Exception("Access token is null")))
                        }
                    }
                }
            } else {
                Log.d("TOKEN_REFRESH", "Token is still valid. No refresh needed.")
                val currentAccessToken = googleAuthState.accessToken
                if (currentAccessToken != null) {
                    continuation.resume(currentAccessToken)
                } else {
                    Log.e("TOKEN_REFRESH", "Access token is null despite no refresh needed.")
                    continuation.resumeWith(Result.failure(Exception("Access token is null")))
                }
            }
        }
    }

    private suspend fun saveCalendarId(calendarId: String) {
        context.dataStore.edit { preferences ->
            preferences[CALENDAR_KEY] = calendarId
        }
    }
    //============= Authorization & Token Handling Logic ===================

    override suspend fun onAuthCodeReceived(
        authService: AuthorizationService,
        tokenRequest: TokenRequest
    ) {
        withContext(Dispatchers.IO) {
            try {
                val tokenResponse = AppAuth.performTokenRequestSuspend(authService, tokenRequest)
                googleAuthState.update(tokenResponse, null)
                if (isAuthenticated()) _authState.update { GoogleAuthState.Authenticated }

            } catch (_: Exception) {
                googleAuthState = AuthState(AppAuth.serviceConfiguration)
            } finally {
                saveTokenResult(googleAuthState)
            }
        }
    }

    private suspend fun saveTokenResult(authState: AuthState) {
        context.dataStore.edit { userValues ->
            userValues[AUTHSTATE_KEY] = authState.jsonSerializeString()
        }
    }

    private fun isAuthenticated(): Boolean {
        return if (googleAuthState.isAuthorized) {
            true
        } else false
    }

    private suspend fun restoreState() {
        val jsonString = context.dataStore.data.first()[AUTHSTATE_KEY]

        if (jsonString != null && !TextUtils.isEmpty(jsonString)) {
            try {
                googleAuthState = AuthState.jsonDeserialize(jsonString)
                Log.d(
                    "TESTII RESTORED",
                    "ACCESS ${googleAuthState.accessToken} REFRESH ${googleAuthState.refreshToken} CONFIG ${googleAuthState.authorizationServiceConfiguration}"
                )
                if (googleAuthState.isAuthorized) _authState.update { GoogleAuthState.Authenticated } else _authState.update { GoogleAuthState.Unauthenticated }

            } catch (_: JSONException) {
            }
        }
    }


    override suspend fun signOut() {
        //Clear DataStore and update State . TODO: Official Sing Out Request has to be sent to google for the correct flow
        context.dataStore.edit { userValues ->
            userValues[AUTHSTATE_KEY] = ""
        }
        _authState.update { GoogleAuthState.Unauthenticated }
    }

    override fun getAuthRequest() {
        _authState.update { GoogleAuthState.Loading }
        val request = AppAuth.getAuthRequest()
        _authorizationRequest.value = request
    }
}

interface IGoogleRepository {
    val authState: Flow<GoogleAuthState>
    val authorizationRequest: Flow<AuthorizationRequest?>
    val plannedWorkouts: Flow<List<PlannedWorkout>>

    suspend fun getCalendarEvents()

    suspend fun planCalendarEvent(startTime: String, endTime: String, workout: Workout)

    suspend fun deleteCalendarEvent(eventId: String)

    suspend fun updateCalendarEvent(
        eventId: String,
        startTime: String,
        endTime: String,
        workout: Workout
    )

    suspend fun signOut()
    fun getAuthRequest()
    suspend fun onAuthCodeReceived(authService: AuthorizationService, tokenRequest: TokenRequest)
}

sealed class GoogleAuthState {
    data object Authenticated : GoogleAuthState()
    data object Unauthenticated : GoogleAuthState()
    data object Loading : GoogleAuthState()
    data class Error(val message: String) : GoogleAuthState()
}