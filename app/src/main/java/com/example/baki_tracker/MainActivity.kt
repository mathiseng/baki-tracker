package com.example.baki_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.baki_tracker.auth.AuthViewModel
import com.example.baki_tracker.dependencyInjection.MainActivityComponent
import com.example.baki_tracker.dependencyInjection.applicationComponent
import com.example.baki_tracker.dependencyInjection.create
import com.example.baki_tracker.repository.AuthRepository
import com.example.baki_tracker.ui.theme.BakiTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainActivityComponent = MainActivityComponent::class.create(applicationComponent)

        val authViewModel: AuthViewModel by viewModels()
        setContent {
            BakiTrackerTheme {
                // A surface container using the 'background' color from the theme
                Scaffold(Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        // RootContainer()
                        TestNavigation(authViewModel = AuthViewModel(AuthRepository()))
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Bonjour $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BakiTrackerTheme {
        Greeting("Android")
    }
}