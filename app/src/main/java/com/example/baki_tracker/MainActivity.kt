package com.example.baki_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.baki_tracker.auth.AuthViewModel
import com.example.baki_tracker.dependencyInjection.MainActivityComponent
import com.example.baki_tracker.dependencyInjection.applicationComponent
import com.example.baki_tracker.dependencyInjection.create
import com.example.baki_tracker.ui.theme.BakiTrackerTheme
import com.google.firebase.FirebaseApp
import com.example.baki_tracker.auth.LoginScreen
import com.example.baki_tracker.dependencyInjection.viewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        val mainActivityComponent = MainActivityComponent::class.create(applicationComponent)
        setContent {
            BakiTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
    composable("login") { LoginScreen(onSuccess = {navController.navigate("home")})}
        composable("home") { GreetingScreen() }
    }
}
@Composable
fun GreetingScreen() {
    val viewmodel = viewModel { AuthViewModel() }
    val uiState = viewmodel.uiState.value
    val user = FirebaseAuth.getInstance().currentUser
    val name = uiState.user?.displayName
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Greeting(user?.displayName)
    }
}
@Composable
fun Greeting(name: String?, modifier: Modifier = Modifier) {
    Text(
        text = "Bonjour $name!",
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BakiTrackerTheme {
        Greeting("Android")
    }
}