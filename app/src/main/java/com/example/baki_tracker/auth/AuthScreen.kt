package com.example.baki_tracker.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.baki_tracker.R
import com.example.baki_tracker.dependencyInjection.viewModel
import me.tatarka.inject.annotations.Inject

typealias AuthScreen = @Composable () -> Unit


@Inject
@Composable
fun AuthScreen(authViewModel: () -> AuthViewModel) {
    val context = LocalContext.current
    val viewModel = viewModel { authViewModel() }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage.isNotBlank()) {
            Toast.makeText(
                context, uiState.errorMessage, Toast.LENGTH_SHORT
            ).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var header = R.string.login
        var footer = R.string.account_does_not_exist
        if (uiState.authMode == AuthMode.SIGNUP) {
            header = R.string.create_account
            footer = R.string.account_exists
        }

        //Heading
        Text(text = "Baki Tracker", fontSize = 32.sp)
        Text(text = stringResource(header), fontSize = 32.sp)
        Spacer(Modifier.height(16.dp))

        //Form
        OutlinedTextField(value = uiState.email,
            onValueChange = { viewModel.changeEmail(it) },
            label = { Text(text = stringResource(R.string.email)) })
        Spacer(Modifier.height(8.dp))

        val isPasswordVisible = uiState.isPasswordVisible
        OutlinedTextField(value = uiState.password,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            onValueChange = { viewModel.changePassword(it) },
            label = { Text(text = stringResource(R.string.password)) },
            trailingIcon = {
                val image =
                    if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

                IconButton(onClick = { viewModel.changePasswordVisibility() }) {
                    Icon(imageVector = image, null)
                }

            })

        //Submit
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                viewModel.authenticate(
                    uiState.authMode, uiState.email, uiState.password
                )
            }, enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(Modifier.size(24.dp))
            } else {
                Text(text = stringResource(header))
            }
        }
        Spacer(Modifier.height(8.dp))

        //Footer
        TextButton(onClick = {
            if (uiState.authMode == AuthMode.LOGIN) {
                viewModel.changeAuthMode(AuthMode.SIGNUP)
            } else viewModel.changeAuthMode(AuthMode.LOGIN)
        }) {
            Text(text = stringResource(footer))
        }
    }
}