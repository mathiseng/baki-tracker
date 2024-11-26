package com.example.baki_tracker.auth

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val _uiState = mutableStateOf(AuthUiState())
    val uiState: State<AuthUiState> = _uiState

    fun launchSignInFlow(): Intent {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )
        return AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
    }

    fun handleSignInResult(resultCode: Int, data: Intent?) {
        println(resultCode)
        val response = IdpResponse.fromResultIntent(data)
        if (resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
           _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                user = user
            )

            Log.d("hurensohn",_uiState.value.user?.email.toString())
            println(_uiState.value.user?.displayName)
        } else {
            _uiState.value = _uiState.value.copy(
                errorMessage = response?.error?.localizedMessage ?: "Sign-in failed"
            )
        }
    }
}
