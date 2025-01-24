package com.example.baki_tracker.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baki_tracker.repository.GoogleAuthState
import com.example.baki_tracker.repository.IAuthRepository
import com.example.baki_tracker.repository.IGoogleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class ProfileViewModel(
    private val authRepository: IAuthRepository,
    private val googleRepository: IGoogleRepository
) : ViewModel() {

    private val _authenticated = MutableStateFlow<GoogleAuthState>(GoogleAuthState.Unauthenticated)
    val authenticated = _authenticated.asStateFlow()


    init {
        viewModelScope.launch {
            googleRepository.authState.collect {
                _authenticated.value = it
            }
        }
    }

    fun logout() {
        authRepository.signOut()
    }

    fun onSignUpWithGoogle() {
        viewModelScope.launch {
            googleRepository.getAuthRequest()
        }
    }


    fun onSignOutWithGoogle() {
        viewModelScope.launch {
            googleRepository.signOut()
        }
    }

    fun test() {
        viewModelScope.launch {
          //  googleRepository.makeApiCall()
        }
    }
}