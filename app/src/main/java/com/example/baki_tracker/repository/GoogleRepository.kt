package com.example.baki_tracker.repository

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.baki_tracker.dependencyInjection.Singleton
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
import org.json.JSONException


@Inject
@Singleton
class GoogleRepository(val context: Context) : IGoogleRepository {

    private val _authState = MutableStateFlow<GoogleAuthState>(GoogleAuthState.Unauthenticated)
    override var authState: Flow<GoogleAuthState> = _authState.asStateFlow()
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _authorizationRequest: MutableStateFlow<AuthorizationRequest?> = MutableStateFlow(null)
    override val authorizationRequest: Flow<AuthorizationRequest?> = _authorizationRequest
    private val storageName = context.packageName + "_preferences"

    //Save Credentials in DataStore
    private val Context.dataStore by
    preferencesDataStore(
        name = storageName,
        produceMigrations = { context ->
            listOf(SharedPreferencesMigration(context, storageName))
        },
    )

    //val client = OkHttpClient.Builder().build()

    private var googleAuthState: AuthState = AuthState()

    private val AUTHSTATE_KEY = stringPreferencesKey("authState")

    //val authService = AuthorizationService(context)

    init {
        repositoryScope.launch {
            restoreState()
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
                googleAuthState = AuthState()
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
                    "GOOGLE API TOKEN RESTORED",
                    "${googleAuthState.accessToken} ${googleAuthState.refreshToken}"
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