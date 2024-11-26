package com.example.baki_tracker.auth

import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

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
        val response = IdpResponse.fromResultIntent(data)
        if (resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                user = user
            )
        } else {
            _uiState.value = _uiState.value.copy(
                errorMessage = response?.error?.localizedMessage ?: "Sign-in failed"
            )
        }
    }
}
