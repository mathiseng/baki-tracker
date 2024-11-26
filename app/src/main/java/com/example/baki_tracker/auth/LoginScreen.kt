package com.example.baki_tracker.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.baki_tracker.dependencyInjection.viewModel

@Composable
fun LoginScreen(onSuccess: () -> Unit) {
    val viewmodel = viewModel { AuthViewModel() }
    val uiState = viewmodel.uiState.value

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewmodel.handleSignInResult(result.resultCode, result.data)
    }

    if (uiState.isAuthenticated) {
        onSuccess()
    }

   LaunchedEffect(Unit) {
       val intent = viewmodel.launchSignInFlow()
       launcher.launch(intent)
   }

    uiState.errorMessage?.let { errorMessage ->
        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
    }
}