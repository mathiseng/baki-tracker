package com.example.baki_tracker.nutrition.tracking

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.baki_tracker.nutrition.NutritionViewModel
import me.tatarka.inject.annotations.Inject

typealias TrackingScreen = @Composable () -> Unit

@Inject
@Composable
fun TrackingScreen(nutritionViewModel: () -> NutritionViewModel) {

    val viewModel = viewModel {nutritionViewModel()}
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val navController = rememberNavController()

    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = uiState.searchText,
                onValueChange = { viewModel.updateSearchText(it) },
                label = { Text("Search for a food item") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            IconButton(onClick = {
                Log.d("barcode", "barcode")
               navController.navigate("barcodeScanner")
            }) {
                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Open Camera")
            }
        }
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
