package com.example.baki_tracker.nutrition

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baki_tracker.repository.TrackingRepository
import com.example.baki_tracker.repository.TrackingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class NutritionViewModel(private val trackingRepository: TrackingRepository, private val context: Context) : ViewModel() {

    private val _uiState = MutableStateFlow(NutritionUiState.initialUiState())
    val uiState: StateFlow<NutritionUiState> = _uiState.asStateFlow()

    fun updateSearchText(text: String) {
        _uiState.update { it.copy(searchText = text) }
    }

    fun searchFood() {
        val query = uiState.value.searchText
        if (query.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Search query cannot be empty") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            trackingRepository.searchFood(query)
            trackingRepository.trackingState.collect { state ->
                when (state) {
                    is TrackingState.Idle -> _uiState.update { it.copy(isLoading = false) }
                    is TrackingState.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is TrackingState.Results -> _uiState.update {
                        it.copy(
                            searchResults = state.results,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                    is TrackingState.Error -> _uiState.update {
                        it.copy(isLoading = false, errorMessage = state.message)
                    }
                }
            }
        }
    }
    fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        context.startActivity(cameraIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

}
