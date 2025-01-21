package com.example.baki_tracker.nutrition

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import com.example.baki_tracker.dependencyInjection.viewModel
import com.example.baki_tracker.navigation.NutritionNavGraph
import com.example.baki_tracker.navigation.destinations.NutritionTabDestinations
import com.example.baki_tracker.nutrition.history.HistoryScreen
import com.example.baki_tracker.nutrition.history.details.HistoryDetailsContainer
import com.example.baki_tracker.nutrition.tracking.TrackingScreen
import com.example.baki_tracker.workout.components.CustomConfirmationDialog
import com.example.components.TabBar
import me.tatarka.inject.annotations.Inject

typealias RootNutritionContainer = @Composable () -> Unit


@Inject
@Composable
fun RootNutritionContainer(rootNutritionViewModel:() ->  RootNutritionViewModel,trackingScreen: TrackingScreen, historyScreen: HistoryScreen, historyDetailsContainer: HistoryDetailsContainer) {

    val navController = rememberNavController()

    val viewModel = viewModel { rootNutritionViewModel() }
    val uiState by viewModel.uiState.collectAsState()
    Column {
        TabBar(
            NutritionTabDestinations.entries,
            navController = navController
        )

        NutritionNavGraph(navController, trackingScreen, historyScreen)
    }


    if (uiState.selectedBottomSheet != NutritionBottomSheet.NONE) {
        historyDetailsContainer()
    }

    uiState.dialogInfo?.let {
        CustomConfirmationDialog(
            stringResource(it.title),
            stringResource(it.description),
            stringResource(it.confirmButtonLabel),
            stringResource(it.dismissButtonLabel),
            it.onConfirm,
            it.onDismiss
        )
    }
}