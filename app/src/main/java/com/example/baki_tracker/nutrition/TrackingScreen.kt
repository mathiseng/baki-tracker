package com.example.baki_tracker.nutrition

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import me.tatarka.inject.annotations.Inject

typealias TrackingScreen = @Composable () -> Unit

@Inject
@Composable
fun TrackingScreen(trackingViewModel: () -> TrackingViewModel) {

    val viewModel = viewModel {trackingViewModel()}
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = uiState.searchText,
            onValueChange = { viewModel.updateSearchText(it) },
            label = { Text("Search for a food item") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.searchFood() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Text("Loading...")
        }

        uiState.errorMessage?.let { error ->
            Text(error, color = androidx.compose.ui.graphics.Color.Red)
        }

        uiState.searchResults.forEach { result ->
            Text(result, modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}
