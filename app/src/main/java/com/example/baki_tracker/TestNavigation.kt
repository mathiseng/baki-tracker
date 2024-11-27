package com.example.baki_tracker

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.baki_tracker.auth.AuthViewModel
import com.example.baki_tracker.auth.LoginScreen
import com.example.baki_tracker.auth.SignupScreen

@Composable
fun TestNavigation(authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController, authViewModel)
        }
        composable("signup") {
            SignupScreen(navController, authViewModel)
        }
        composable("home") {
            TestHomeScreen(navController, authViewModel)
        }

    }
}