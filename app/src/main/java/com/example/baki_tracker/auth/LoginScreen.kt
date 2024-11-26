package com.example.baki_tracker.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.baki_tracker.dependencyInjection.viewModel

@Composable
fun LoginScreen(authViewModel: () -> AuthViewModel, onSuccess: () -> Unit) {
    val viewmodel = viewModel { authViewModel() }
    val uiState by viewmodel.uiState.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewmodel.handleSignInResult(result.resultCode, result.data)
    }

    if (uiState.isAuthenticated) {
        onSuccess()
    }

    // Your UI layout
    Button(onClick = { launcher.launch(viewmodel.launchSignInFlow()) }) {
        Text("Sign In")
    }

    uiState.errorMessage?.let { errorMessage ->
        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
    }
}