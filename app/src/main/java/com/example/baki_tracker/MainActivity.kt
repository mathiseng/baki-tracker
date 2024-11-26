package com.example.baki_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.baki_tracker.dependencyInjection.MainActivityComponent
import com.example.baki_tracker.dependencyInjection.applicationComponent
import com.example.baki_tracker.dependencyInjection.create
import com.example.baki_tracker.ui.theme.BakiTrackerTheme
import com.google.firebase.FirebaseApp

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
fun LoginScreen(onSuccess: () -> Unit) {
    // Fake login logic (replace with real Firebase Authentication logic)
    val isLoggedIn = false // Replace with your AuthViewModel state
    if (isLoggedIn) {
        onSuccess() // Navigate to the next screen when login is successful
    }

    // Login UI
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Text("Login Screen", style = MaterialTheme.typography.headlineMedium)
        // Add actual UI elements for email/password login or Firebase login button
    }
}
@Composable
fun GreetingScreen() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Greeting("Android")
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