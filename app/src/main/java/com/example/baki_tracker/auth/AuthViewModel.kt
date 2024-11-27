package com.example.baki_tracker.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

@Inject
class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState: MutableStateFlow<AuthState> =
        MutableStateFlow(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.update { AuthState.Unauthenticated }
        } else {
            _authState.update { AuthState.Authenticated }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.update {
                AuthState.Error("Email or password can't be empty")
                return
            }
        }
        _authState.update { AuthState.Loading }
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _authState.update { AuthState.Authenticated }
            } else {
                _authState.update {
                    AuthState.Error(
                        task.exception?.message ?: "Something went wrong"
                    )
                }
            }
        }
    }

    fun signup(email: String, password: String) {
        Log.d("Testoo", email + " " + password)
        if (email.isBlank() || password.isBlank()) {
            _authState.update {
                AuthState.Error("Email or password can't be empty")
                return
            }
        }
        _authState.update { AuthState.Loading }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            Log.d("Testoo COMPLETE", task.toString())

            if (task.isSuccessful) {
                Log.d("Testoo Success", task.exception?.message ?: "")

                _authState.update { AuthState.Authenticated }
            } else {
                _authState.update {
                    Log.d("Testoo ERR", task.exception?.message ?: "")

                    AuthState.Error(
                        task.exception?.message ?: "Something went wrong"
                    )
                }
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _authState.update { AuthState.Unauthenticated }
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}