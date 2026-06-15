package com.jeiel85.daddyweekend.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.jeiel85.daddyweekend.ui.screens.*
import com.jeiel85.daddyweekend.ui.viewmodel.MainViewModel

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "홈", Icons.Default.Home)
    object Saved : Screen("saved", "내 코스첩", Icons.Default.Weekend)
    object Templates : Screen("templates", "도감", Icons.Default.LibraryBooks)
    object Settings : Screen("settings", "설정", Icons.Default.Settings)
}

@Composable
fun AppNavigation(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    // Observe active nav destination to toggle bottom bar state
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Primary bottom navigation items
    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Saved,
        Screen.Templates,
        Screen.Settings
    )

    // Hide bottom bar on depth details (conditions / results screens)
    val shouldShowBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = shouldShowBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar(
                    modifier = Modifier.testTag("app_navigation_bar")
                ) {
                    bottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            modifier = Modifier.testTag("nav_item_${screen.route}")
                        )
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Home
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToConditions = {
                        navController.navigate("conditions")
                    },
                    onNavigateToResults = { age, duration, budget, place, energy ->
                        navController.navigate("results/$age/$duration/$budget/$place/$energy")
                    },
                    onNavigateToMyCourses = {
                        navController.navigate(Screen.Saved.route)
                    },
                    onNavigateToTemplates = {
                        navController.navigate(Screen.Templates.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onNavigateToSavedDetail = { _ ->
                        navController.navigate(Screen.Saved.route)
                    }
                )
            }

            // 2. Conditions Selection
            composable("conditions") {
                ConditionScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToResults = { age, duration, budget, place, energy ->
                        navController.navigate("results/$age/$duration/$budget/$place/$energy")
                    }
                )
            }

            // 3. Recommended results
            composable(
                route = "results/{age}/{duration}/{budget}/{place}/{energy}",
                arguments = listOf(
                    navArgument("age") { type = NavType.StringType },
                    navArgument("duration") { type = NavType.StringType },
                    navArgument("budget") { type = NavType.StringType },
                    navArgument("place") { type = NavType.StringType },
                    navArgument("energy") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val age = backStackEntry.arguments?.getString("age") ?: "유치원"
                val duration = backStackEntry.arguments?.getString("duration") ?: "전체"
                val budget = backStackEntry.arguments?.getString("budget") ?: "전체"
                val place = backStackEntry.arguments?.getString("place") ?: "전체"
                val energy = backStackEntry.arguments?.getString("energy") ?: "보통"

                ResultScreen(
                    viewModel = viewModel,
                    age = age,
                    duration = duration,
                    budget = budget,
                    place = place,
                    energy = energy,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMyCourses = {
                        navController.navigate(Screen.Saved.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }

            // 4. My scrap cards
            composable(Screen.Saved.route) {
                MyCoursesScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            // 5. Template Library
            composable(Screen.Templates.route) {
                TemplateManagerScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            // 6. Settings
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
