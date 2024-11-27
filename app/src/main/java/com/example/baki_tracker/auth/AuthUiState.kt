package com.example.baki_tracker.auth

data class AuthUiState(
    val authMode: AuthMode,
    val email: String,
    val password: String,
    val errorMessage: String,
    val isPasswordVisible: Boolean,
    val isLoading: Boolean,
    val isAuthenticated: Boolean
) {
    companion object {
        fun initialUiState() = AuthUiState(AuthMode.LOGIN, "", "", "", false, false, false)
    }
}

//not necessary but useful for better readability (Type-safety)
enum class AuthMode {
    SIGNUP, LOGIN
}