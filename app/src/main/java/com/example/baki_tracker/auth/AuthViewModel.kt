package com.example.baki_tracker.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baki_tracker.repository.AuthState
import com.example.baki_tracker.repository.IAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class AuthViewModel(private val authRepository: IAuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState.initialUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        authRepository.checkAuthStatus()
        viewModelScope.launch {
            authRepository.authState.collect { authState ->
                when (authState) {
                    is AuthState.Error -> _uiState.update {
                        it.copy(
                            errorMessage = authState.message, isLoading = false
                        )
                    }

                    is AuthState.Loading -> _uiState.update {
                        it.copy(
                            errorMessage = "", isLoading = true
                        )
                    }

                    else -> _uiState.update {
                        it.copy(
                            errorMessage = "",
                            isAuthenticated = authState == AuthState.Authenticated,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun authenticate(authMode: AuthMode, email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Email or password can't be empty")
            }
            return
        }

        if (authMode == AuthMode.LOGIN) {
            authRepository.login(email, password)
        } else {
            authRepository.signup(email, password)
        }
    }

    fun changeAuthMode(mode: AuthMode) {
        _uiState.update { it.copy(authMode = mode) }
    }

    fun changePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun changeEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun signOut() {
        authRepository.signOut()
    }

    fun changePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }
}
