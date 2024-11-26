package com.example.baki_tracker

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.baki_tracker.navigation.destinations.RootDestinations
import com.example.baki_tracker.navigation.RootNavGraph

@Composable
fun RootContainer() {
    val navController = rememberNavController()

    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    NavigationSuiteScaffold(navigationSuiteItems = {
        RootDestinations.entries.forEachIndexed { index, screen ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.hasRoute(screen.route::class)
            } == true

            item(selected = isSelected, onClick = {
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }, icon = {
                if (isSelected) {
                    Icon(painterResource(screen.selectedIcon), "")
                } else {
                    Icon(painterResource(screen.unselectedIcon), "")
                }
            }, label = {
                Text(text = stringResource(screen.label))
            })
        }
    }) {
        RootNavGraph(navController)
    }
}