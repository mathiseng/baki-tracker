package com.example.baki_tracker.workout.workouts.options

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.baki_tracker.dependencyInjection.viewModel
import me.tatarka.inject.annotations.Inject

typealias OptionsContainer = @Composable () -> Unit


@OptIn(ExperimentalMaterial3Api::class)
@Inject
@Composable
fun OptionsContainer(optionsViewModel: () -> OptionsViewModel) {
    val viewModel = viewModel { optionsViewModel() }
    val uiState by viewModel.uiState.collectAsState()

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        modifier = Modifier.statusBarsPadding(),
        onDismissRequest = { viewModel.onDismiss() },
        sheetState = sheetState
    ) {
        OptionsScreen(
            uiState = uiState,
            onEditClick = viewModel::onEditClick,
            onDeleteClick = viewModel::onDeleteClick,
            )
    }
}


