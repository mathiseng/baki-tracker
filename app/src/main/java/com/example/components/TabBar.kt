package com.example.components

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.baki_tracker.navigation.destinations.TabDestination
import kotlin.enums.EnumEntries

@Composable
fun <T> TabBar(
    tabs: EnumEntries<T>, navController: NavController
) where T : Enum<T>, T : TabDestination {

    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    val selectedTabIndex = tabs.indexOfFirst { screen ->
        currentDestination?.hierarchy?.any { it.hasRoute(screen.route::class) } == true
    }.coerceAtLeast(0) // Default to the first tab if no match is found

    TabRow(
        selectedTabIndex = selectedTabIndex,
    ) {

        tabs.forEachIndexed { index, screen ->
            Tab(
                selected = index == selectedTabIndex,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                text = { Text(text = stringResource(screen.label)) },
            )
        }
    }
}