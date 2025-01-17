package com.example.baki_tracker.nutrition.tracking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.baki_tracker.nutrition.FoodItem
import com.example.baki_tracker.nutrition.NutritionViewModel
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

typealias TrackingScreen = @Composable () -> Unit

@OptIn(ExperimentalMaterial3Api::class)
@Inject
@Composable
fun TrackingScreen(nutritionViewModel: () -> NutritionViewModel, scanScreen: ScanScreen) {

    val viewModel = viewModel {nutritionViewModel()}
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // State for the modal bottom sheet
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        if(uiState.showBarcodeScanner) {scanScreen({barcode -> viewModel.searchFoodBarcode(barcode)}) }
        else {
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
                    viewModel.onShowBarcodeScannerChange(true)
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

            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.searchResults) { foodItem ->
                    FoodItemCard(
                        name = foodItem.name,
                        quantity = "${foodItem.quantity} unit(s)",
                        calories = "${foodItem.calories} kcal",
                        onClick = {
                            // Set selected food item and show bottom sheet
                            viewModel.onFoodItemSelectionChange(foodItem)
                            coroutineScope.launch { sheetState.show() }
                        }
                    )
                }
            }
        }
}

    // Modal Bottom Sheet
    uiState.selectedFoodItem?.let {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onFoodItemSelectionChange(null) },
            sheetState = sheetState
        ) {
            FoodDetailsBottomSheetContent(
                foodItem = it,
                viewModel = viewModel,
                onDismiss = { viewModel.onFoodItemSelectionChange(null) }
            )
        }
    }
}

@Composable
fun FoodDetailsBottomSheetContent(
    foodItem: FoodItem,
    viewModel: NutritionViewModel,
    onDismiss: () -> Unit,

) {
    // State for editable quantity (as a String to handle input properly)
    val quantityInGrams = remember { mutableStateOf(foodItem.quantity.toInt().toString()) }

    // Dynamically calculate the scaled values
    val quantityAsInt = quantityInGrams.value.toIntOrNull() ?: 0
    val scaledCalories = foodItem.calories * quantityAsInt
    val scaledProtein = foodItem.protein * quantityAsInt
    val scaledCarbs = foodItem.carbs * quantityAsInt
    val scaledFat = foodItem.fat * quantityAsInt


    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = foodItem.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)


        // Input field for quantity
        TextField(
            value = quantityInGrams.value,
            onValueChange = { input ->
                // Allow only numeric input
                if (input.all { it.isDigit() }) {
                    quantityInGrams.value = input
                }
            },
            label = { Text("Amount (g)") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        // Nutritional Information
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            NutrientCard("Kalorien", "${scaledCalories.toInt()} kcal")
            NutrientCard("Fett", String.format("%.2f g", scaledFat))
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            NutrientCard("Carbs", String.format("%.2f g", scaledCarbs))
            NutrientCard("Eiweiß", String.format("%.2f g", scaledProtein))
        }

        // Save and Cancel buttons
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        ) {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
            Button(onClick = {
                val updatedQuantity = quantityAsInt.toFloat() // Convert back to Float
                val updatedFoodItem = foodItem.copy(quantity = updatedQuantity)
                viewModel.saveFoodItemToDay(updatedFoodItem) // Example save logic
                onDismiss()
            }) {
                Text("Save")
            }

        }
    }
}

@Composable
fun NutrientCard(label: String, value: String) {
    Card{
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, fontWeight = FontWeight.Bold)
            Text(text = value)
        }
    }
}
