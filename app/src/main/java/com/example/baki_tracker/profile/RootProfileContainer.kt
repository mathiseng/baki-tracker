package com.example.baki_tracker.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.baki_tracker.R
import com.example.baki_tracker.dependencyInjection.viewModel
import me.tatarka.inject.annotations.Inject

typealias RootProfileContainer = @Composable () -> Unit

@Inject
@Composable
fun RootProfileContainer(profileViewModel: () -> ProfileViewModel) {
    val viewModel = viewModel { profileViewModel() }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { viewModel.logout() }) { Text(stringResource(R.string.signout)) }
    }
}