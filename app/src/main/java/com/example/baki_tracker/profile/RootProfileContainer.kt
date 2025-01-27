package com.example.baki_tracker.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
            OutlinedButton( modifier = Modifier
                .padding(vertical = 8.dp)
                .height(height = 48.dp),
                onClick = { viewModel.onSignOutWithGoogle() }, colors = ButtonColors(
                    MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor =  MaterialTheme.colorScheme.surface,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Image(
                    modifier = Modifier.padding(end = 4.dp),
                    painter = painterResource(R.drawable.ic_google_logo),
                    contentDescription = ""
                )
                Text("Sign Out from google")
            }
        } else {
            OutlinedButton( modifier = Modifier
                .padding(vertical = 8.dp)
                .height(height = 48.dp),
                onClick = { viewModel.onSignUpWithGoogle() }, colors = ButtonColors(
                    MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Image(
                    modifier = Modifier.padding(end = 4.dp),
                    painter = painterResource(R.drawable.ic_google_logo),
                    contentDescription = ""
                )
                Text("Sign up with google")
            }
        }
    }
}