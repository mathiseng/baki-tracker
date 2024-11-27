package com.example.baki_tracker.repository

import com.example.baki_tracker.dependencyInjection.Singleton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

@Inject
@Singleton
class AuthRepository() : IAuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    override val authState: Flow<AuthState> = _authState.asStateFlow()

    override fun login(email: String, password: String) {
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

    override fun signup(email: String, password: String) {
        _authState.update { AuthState.Loading }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
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

    override fun signOut() {
        auth.signOut()
        _authState.update { AuthState.Unauthenticated }
    }

    override fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.update { AuthState.Unauthenticated }
        } else {
            _authState.update { AuthState.Authenticated }
        }
    }
}

interface IAuthRepository {
    val authState: Flow<AuthState>
    fun login(email: String, password: String)
    fun signup(email: String, password: String)
    fun signOut()
    fun checkAuthStatus()
}

sealed class AuthState {
    data object Authenticated : AuthState()
    data object Unauthenticated : AuthState()
    data object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}