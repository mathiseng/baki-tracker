package com.example.baki_tracker.profile

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.baki_tracker.R
import com.example.baki_tracker.dependencyInjection.viewModel
import com.example.baki_tracker.repository.GoogleAuthState
import me.tatarka.inject.annotations.Inject

typealias RootProfileContainer = @Composable () -> Unit

@Inject
@Composable
fun RootProfileContainer(profileViewModel: () -> ProfileViewModel) {
    val viewModel = viewModel { profileViewModel() }
    val authenticated by viewModel.authenticated.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { viewModel.logout() }) { Text(stringResource(R.string.signout)) }
        Log.d("TESTII COMPOSABLE", "$authenticated")
        if (authenticated is GoogleAuthState.Authenticated) {
            Button(onClick = { viewModel.onSignOutWithGoogle() }) { Text("Sign Out from google") }
        } else {
            Button(onClick = { viewModel.onSignUpWithGoogle() }) { Text("Sign up with google") }

        }

        Button(onClick = { viewModel.test() }) { Text("Test") }
    }
}