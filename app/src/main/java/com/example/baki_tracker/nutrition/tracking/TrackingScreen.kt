package com.example.baki_tracker.nutrition.tracking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.baki_tracker.model.nutrition.FoodItem
import com.example.baki_tracker.nutrition.NutritionViewModel
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

typealias TrackingScreen = @Composable () -> Unit

@OptIn(ExperimentalMaterial3Api::class)
@Inject
@Composable
fun TrackingScreen(nutritionViewModel: () -> NutritionViewModel, scanScreen: ScanScreen) {

    val viewModel = viewModel { nutritionViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // State for the modal bottom sheet
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        if (uiState.showBarcodeScanner) {
            Box(Modifier.fillMaxSize()) {
                scanScreen({ barcode -> viewModel.searchFoodBarcode(barcode) })
                IconButton(modifier = Modifier.align(Alignment.TopStart),
                    onClick = { viewModel.onShowBarcodeScannerChange(false) }) {
                    Icon(
                        Icons.Default.Clear, null, tint = Color.White
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBar(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = uiState.searchText,
                            onSearch = { viewModel.searchFood() },
                            expanded = true,
                            onExpandedChange = { },
                            trailingIcon = {
                                if (uiState.searchText.isNotBlank()) {
                                    IconButton(onClick = {
                                        viewModel.updateSearchResults(
                                            emptyList()
                                        )
                                        viewModel.updateSearchText("")
                                    }) { Icon(Icons.Default.Clear, null) }
                                }
                            },
                            placeholder = { Text("Search for a food item", maxLines = 1) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            onQueryChange = { viewModel.updateSearchText(it) },
                        )
                    },
                    expanded = false,
                    onExpandedChange = { },
                ) {}

                IconButton(onClick = {
                    viewModel.onShowBarcodeScannerChange(true)
                }) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Open Camera"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Text(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(), text = "Loading..."
                )
            }

            uiState.errorMessage?.let { error ->
                Text(error, color = MaterialTheme.colorScheme.error)
            }

            LazyColumn(
                contentPadding = PaddingValues(vertical = 0.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.searchResults) { foodItem ->
                    FoodItemCard(
                        foodItem,
                        onClick = {
                            // Set selected food item and show bottom sheet
                            viewModel.onFoodItemSelectionChange(foodItem)
                            coroutineScope.launch { sheetState.show() }
                        })
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
            FoodDetailsBottomSheetContent(foodItem = it,
                onSaveFoodItem = {foodItem-> viewModel.saveFoodItemToDay(foodItem)},
                onDismiss = { viewModel.onFoodItemSelectionChange(null) })
        }
    }
}

@Composable
fun FoodDetailsBottomSheetContent(
    foodItem: FoodItem,
    onSaveFoodItem: (FoodItem) -> Unit,
    onDismiss: () -> Unit,
) {
    // State for editable quantity (as a String to handle input properly)
    val quantityInGrams = remember { mutableStateOf((foodItem.quantity.toInt() * 100).toString()) }

    // Dynamically calculate the scaled values
    val quantityAsInt = quantityInGrams.value.toIntOrNull() ?: 0
    val scaledCalories = foodItem.calories * quantityAsInt
    val scaledProtein = foodItem.protein * quantityAsInt
    val scaledCarbs = foodItem.carbs * quantityAsInt
    val scaledFat = foodItem.fat * quantityAsInt


    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = foodItem.name, fontSize = 20.sp, fontWeight = FontWeight.Bold
        )
        // Input field for quantity
        TextField(value = (quantityInGrams.value),
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

        val nutrients = listOf(
            "Kalorien" to "$scaledCalories kcal",
            "Fett" to String.format("%.2f g", scaledFat),
            "Kohlenhydrate" to String.format("%.2f g", scaledCarbs),
            "Eiweiß" to String.format("%.2f g", scaledProtein)
        )
        Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 2 Spalten
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(nutrients) { nutrient ->
                NutrientCard(nutrient.first, nutrient.second)
            }
        }

        HorizontalDivider(Modifier.padding(top = 16.dp))
        //Submit Buttons
        Row(modifier = Modifier.padding(vertical = 16.dp)) {
            Button(onClick = {
                val updatedQuantity = quantityAsInt.toFloat() // Convert back to Float
                val updatedFoodItem = foodItem.copy(quantity = updatedQuantity)
                onSaveFoodItem(updatedFoodItem)
                onDismiss()
            }) { Text("Save") }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = { onDismiss() }) { Text("Cancel") }
        }
    }
}

@Composable
fun NutrientCard(label: String, value: String) {
    Card(Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, fontWeight = FontWeight.Bold)
            Text(text = value)
        }
    }
}
