package com.example.baki_tracker.profile

import androidx.lifecycle.ViewModel
import com.example.baki_tracker.repository.IAuthRepository
import me.tatarka.inject.annotations.Inject

@Inject
class ProfileViewModel(private val authRepository: IAuthRepository) : ViewModel() {
    fun logout() {
        authRepository.signOut()
    }
}