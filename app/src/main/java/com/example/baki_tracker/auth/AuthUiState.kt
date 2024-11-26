package com.example.baki_tracker.auth

import com.google.firebase.auth.FirebaseUser

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val user: FirebaseUser? = null,
    val errorMessage: String? = null

)
