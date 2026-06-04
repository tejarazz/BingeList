package com.example.bingelist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bingelist.presentation.detail.DetailScreen
import com.example.bingelist.presentation.search.SearchScreen
import com.example.bingelist.presentation.watchlist.WatchlistScreen
import com.example.bingelist.ui.theme.BingeListTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BingeListTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentDestination == "watchlist",
                                onClick = { navController.navigate("watchlist") },
                                icon = { Icon(Icons.Default.List, contentDescription = "Watchlist") },
                                label = { Text("Watchlist") }
                            )
                            NavigationBarItem(
                                selected = currentDestination == "search",
                                onClick = { navController.navigate("search") },
                                icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                                label = { Text("Search") }
                            )
                        }
                    }
                ) { innerPadding ->
                    SharedTransitionLayout {
                        NavHost(
                            navController = navController,
                            startDestination = "watchlist",
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable("watchlist") {
                                WatchlistScreen(
                                    onDramaClick = { drama ->
                                        navController.navigate("detail/${drama.id}")
                                    },
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    animatedVisibilityScope = this
                                )
                            }
                            composable("search") {
                                SearchScreen(
                                    onDramaClick = { drama ->
                                        navController.navigate("detail/${drama.id}")
                                    },
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    animatedVisibilityScope = this
                                )
                            }
                            composable(
                                route = "detail/{dramaId}",
                                arguments = listOf(navArgument("dramaId") { type = NavType.IntType })
                            ) {
                                DetailScreen(
                                    onBack = { navController.popBackStack() },
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    animatedVisibilityScope = this
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
