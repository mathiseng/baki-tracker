package com.example.baki_tracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.baki_tracker.auth.AuthViewModel

@Composable
fun TestHomeScreen(navController: NavController, authViewModel: AuthViewModel) {


    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.isAuthenticated) {
        if (!uiState.isAuthenticated) {
            navController.navigate("login")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Home")
        Button(onClick = { authViewModel.signOut() }) { Text(text = "Sign out") }
    }
}