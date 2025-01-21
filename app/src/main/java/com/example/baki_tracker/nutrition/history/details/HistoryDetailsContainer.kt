package com.example.baki_tracker.nutrition.history.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.baki_tracker.R
import com.example.baki_tracker.model.nutrition.FoodItem
import com.example.baki_tracker.nutrition.history.FoodSummaryCard
import me.tatarka.inject.annotations.Inject

typealias HistoryDetailsContainer = @Composable () -> Unit


@OptIn(ExperimentalMaterial3Api::class)
@Inject
@Composable
fun HistoryDetailsContainer(historyDetailsViewModel: () -> HistoryDetailsViewModel) {
    val viewModel = viewModel { historyDetailsViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    ModalBottomSheet(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize(), onDismissRequest = {
            viewModel.onDismiss()
        }, sheetState = sheetState
    ) {
        HistoryDetailsScreen(
            uiState,
            viewModel::onDeleteFoodItem,
            viewModel::onEditFoodItem,
            viewModel::onShowEditDialogChange
        )
    }
}


@Composable
fun HistoryDetailsScreen(
    uiState: HistoryDetailsUiState,
    onDeleteFoodItem: (String, String) -> Unit,
    onEditFoodItem: (FoodItem, String) -> Unit,
    onShowEditDialogChange: (FoodItem?) -> Unit
) {
    uiState.selectedFoodItem?.let {
        EditDialog(modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
            .padding(24.dp),
            foodItem = it,
            { onShowEditDialogChange(null) },
            { foodItem ->
                onEditFoodItem(foodItem, uiState.trackingDayId)
            })
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {

        item {
            Text(uiState.date, fontSize = 22.sp)
            Spacer(Modifier.height(16.dp))
        }
        item {
            FoodSummaryCard(
                foodItems = uiState.foodItems,
                calorieGoal = 2000, // Replace with dynamic goal if available
                proteinGoal = 150,
                carbsGoal = 200,
                fatsGoal = 70,
            )
        }

        item {
            Spacer(Modifier.height(16.dp))
            Text("Meals",  fontSize = 22.sp,style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
        }

        items(uiState.foodItems) {
            FoodDetailsCard(it.name,
                "${it.quantity}",
                "${it.calories}",
                { onDeleteFoodItem(it.uuid, uiState.trackingDayId) }) {
                onShowEditDialogChange(it)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun FoodDetailsCard(
    name: String, quantity: String, calories: String, onDelete: () -> Unit, onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    modifier = Modifier.padding(bottom = 4.dp),
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.W500
                )
                Text(
                    text = "$quantity g - ${calories.toDouble()*quantity.toDouble()} kcal",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            IconButton(onClick = onDelete) { Icon(Icons.Outlined.Delete, null) }
        }
    }
}

@Composable
fun EditDialog(
    modifier: Modifier = Modifier,
    foodItem: FoodItem,
    onDismiss: () -> Unit,
    onEditFoodItem: (FoodItem) -> Unit
) {

    var quantityInGrams by remember { mutableStateOf(foodItem.quantity.toInt().toString()) }

    // Dynamically calculate the scaled values
    val quantityAsInt = quantityInGrams.toIntOrNull() ?: 0

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onEditFoodItem(foodItem.copy(quantity = quantityAsInt.toFloat())) }) {
                Text(stringResource(R.string.edit))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        title = { Text("Edit Food Entry") },
        text = {
            TextField(
                value = quantityInGrams,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        quantityInGrams = input
                    }
                },
                label = { Text("Amount (g)") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        },
    )

}

@Preview
@Composable
fun HistoryDetailsScreenPreview() {
    HistoryDetailsScreen(HistoryDetailsUiState(), { _, _ -> }, onEditFoodItem = { _, _ -> }, {})
}


