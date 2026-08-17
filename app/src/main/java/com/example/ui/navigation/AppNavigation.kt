package com.example.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.R
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.CalculationDetailsScreen
import com.example.ui.screens.GuideDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SavedCalculationsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SolarCalculatorScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.WhatDoINeedScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.PowerTrackGreen
import com.example.ui.theme.PowerTrackGreenContainer
import com.example.ui.theme.TextMutedDark
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import com.example.ui.viewmodel.PowerTrackViewModel
import kotlinx.coroutines.launch

object AppRoutes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val SOLAR_CALCULATOR = "solar_calculator"
    const val WHAT_DO_I_NEED = "what_do_i_need"
    const val SAVED_CALCULATIONS = "saved_calculations"
    const val CALCULATION_DETAILS = "calculation_details"
    const val LEARN = "learn"
    const val GUIDE_DETAIL = "guide_detail/{guideId}"
    const val SETTINGS = "settings"
    const val ABOUT = "about"

    fun guideDetail(guideId: String) = "guide_detail/$guideId"
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    object Home : BottomNavItem(AppRoutes.HOME, "Home", Icons.Default.Home, "nav_home")
    object Saved : BottomNavItem(AppRoutes.SAVED_CALCULATIONS, "Saved", Icons.Default.Bookmark, "nav_saved")
    object Learn : BottomNavItem(AppRoutes.LEARN, "Learn", Icons.Default.MenuBook, "nav_learn")
    object Settings : BottomNavItem(AppRoutes.SETTINGS, "Settings", Icons.Default.Settings, "nav_settings")
}

@Composable
fun PowerTrackApp(viewModel: PowerTrackViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Saved,
        BottomNavItem.Learn,
        BottomNavItem.Settings
    )

    val showBottomBar = currentRoute in listOf(
        AppRoutes.HOME,
        AppRoutes.SAVED_CALCULATIONS,
        AppRoutes.LEARN,
        AppRoutes.SETTINGS
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showBottomBar,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = DarkSurfaceCard,
                drawerContentColor = TextPrimaryDark,
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 24.dp, horizontal = 16.dp)
                ) {
                    // Drawer Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_powertrack_logo),
                            contentDescription = "PowerTrack Logo",
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row {
                                Text(
                                    text = "Power",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimaryDark
                                )
                                Text(
                                    text = "Track",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = PowerTrackGreen
                                )
                            }
                            Text(
                                text = "Solar System Calculator",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DarkSurfaceBorder)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Drawer Items
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Home Dashboard") },
                        selected = currentRoute == AppRoutes.HOME,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(AppRoutes.HOME) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                            }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = PowerTrackGreenContainer,
                            selectedTextColor = PowerTrackGreen,
                            selectedIconColor = PowerTrackGreen,
                            unselectedTextColor = TextPrimaryDark,
                            unselectedIconColor = TextSecondaryDark
                        )
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Calculate, contentDescription = null) },
                        label = { Text("Solar Calculator") },
                        selected = currentRoute == AppRoutes.SOLAR_CALCULATOR,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(AppRoutes.SOLAR_CALCULATOR)
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = PowerTrackGreenContainer,
                            selectedTextColor = PowerTrackGreen,
                            selectedIconColor = PowerTrackGreen,
                            unselectedTextColor = TextPrimaryDark,
                            unselectedIconColor = TextSecondaryDark
                        )
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Search, contentDescription = null) },
                        label = { Text("What Do I Need?") },
                        selected = currentRoute == AppRoutes.WHAT_DO_I_NEED,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(AppRoutes.WHAT_DO_I_NEED)
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = PowerTrackGreenContainer,
                            selectedTextColor = PowerTrackGreen,
                            selectedIconColor = PowerTrackGreen,
                            unselectedTextColor = TextPrimaryDark,
                            unselectedIconColor = TextSecondaryDark
                        )
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Bookmark, contentDescription = null) },
                        label = { Text("Saved Calculations") },
                        selected = currentRoute == AppRoutes.SAVED_CALCULATIONS,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(AppRoutes.SAVED_CALCULATIONS) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                            }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = PowerTrackGreenContainer,
                            selectedTextColor = PowerTrackGreen,
                            selectedIconColor = PowerTrackGreen,
                            unselectedTextColor = TextPrimaryDark,
                            unselectedIconColor = TextSecondaryDark
                        )
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                        label = { Text("Solar Guides & Learn") },
                        selected = currentRoute == AppRoutes.LEARN,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(AppRoutes.LEARN) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                            }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = PowerTrackGreenContainer,
                            selectedTextColor = PowerTrackGreen,
                            selectedIconColor = PowerTrackGreen,
                            unselectedTextColor = TextPrimaryDark,
                            unselectedIconColor = TextSecondaryDark
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    HorizontalDivider(color = DarkSurfaceBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        label = { Text("Settings") },
                        selected = currentRoute == AppRoutes.SETTINGS,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(AppRoutes.SETTINGS)
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = PowerTrackGreenContainer,
                            selectedTextColor = PowerTrackGreen,
                            selectedIconColor = PowerTrackGreen,
                            unselectedTextColor = TextPrimaryDark,
                            unselectedIconColor = TextSecondaryDark
                        )
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Info, contentDescription = null) },
                        label = { Text("About PowerTrack") },
                        selected = currentRoute == AppRoutes.ABOUT,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(AppRoutes.ABOUT)
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = PowerTrackGreenContainer,
                            selectedTextColor = PowerTrackGreen,
                            selectedIconColor = PowerTrackGreen,
                            unselectedTextColor = TextPrimaryDark,
                            unselectedIconColor = TextSecondaryDark
                        )
                    )
                }
            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = DarkSurfaceElevated,
                        contentColor = TextPrimaryDark,
                        tonalElevation = 8.dp
                    ) {
                        bottomNavItems.forEach { item ->
                            val isSelected = currentRoute == item.route
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    if (currentRoute != item.route) {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PowerTrackGreen,
                                    selectedTextColor = PowerTrackGreen,
                                    indicatorColor = PowerTrackGreenContainer,
                                    unselectedIconColor = TextMutedDark,
                                    unselectedTextColor = TextMutedDark
                                ),
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AppRoutes.SPLASH,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(AppRoutes.SPLASH) {
                    SplashScreen(
                        onTimeout = {
                            if (viewModel.hasCompletedOnboarding) {
                                navController.navigate(AppRoutes.HOME) {
                                    popUpTo(AppRoutes.SPLASH) { inclusive = true }
                                }
                            } else {
                                navController.navigate(AppRoutes.ONBOARDING) {
                                    popUpTo(AppRoutes.SPLASH) { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable(AppRoutes.ONBOARDING) {
                    OnboardingScreen(
                        onGetStarted = {
                            viewModel.completeOnboarding()
                            navController.navigate(AppRoutes.HOME) {
                                popUpTo(AppRoutes.ONBOARDING) { inclusive = true }
                            }
                        },
                        onSkip = {
                            viewModel.completeOnboarding()
                            navController.navigate(AppRoutes.HOME) {
                                popUpTo(AppRoutes.ONBOARDING) { inclusive = true }
                            }
                        }
                    )
                }

                composable(AppRoutes.HOME) {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToSolarCalculator = { navController.navigate(AppRoutes.SOLAR_CALCULATOR) },
                        onNavigateToWhatDoINeed = { navController.navigate(AppRoutes.WHAT_DO_I_NEED) },
                        onNavigateToSaved = { navController.navigate(AppRoutes.SAVED_CALCULATIONS) },
                        onNavigateToLearn = { navController.navigate(AppRoutes.LEARN) },
                        onNavigateToSettings = { navController.navigate(AppRoutes.SETTINGS) },
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }

                composable(AppRoutes.SOLAR_CALCULATOR) {
                    SolarCalculatorScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToSaved = {
                            navController.navigate(AppRoutes.SAVED_CALCULATIONS) {
                                popUpTo(AppRoutes.HOME)
                            }
                        }
                    )
                }

                composable(AppRoutes.WHAT_DO_I_NEED) {
                    WhatDoINeedScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToSaved = {
                            navController.navigate(AppRoutes.SAVED_CALCULATIONS) {
                                popUpTo(AppRoutes.HOME)
                            }
                        }
                    )
                }

                composable(AppRoutes.SAVED_CALCULATIONS) {
                    SavedCalculationsScreen(
                        viewModel = viewModel,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onNavigateToSolarCalc = { navController.navigate(AppRoutes.SOLAR_CALCULATOR) },
                        onSelectCalculation = { calc ->
                            viewModel.selectSavedCalculation(calc)
                            navController.navigate(AppRoutes.CALCULATION_DETAILS)
                        }
                    )
                }

                composable(AppRoutes.CALCULATION_DETAILS) {
                    CalculationDetailsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(AppRoutes.LEARN) {
                    LearnScreen(
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onSelectGuide = { guideId ->
                            navController.navigate(AppRoutes.guideDetail(guideId))
                        }
                    )
                }

                composable(
                    route = AppRoutes.GUIDE_DETAIL,
                    arguments = listOf(navArgument("guideId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val guideId = backStackEntry.arguments?.getString("guideId") ?: ""
                    GuideDetailScreen(
                        guideId = guideId,
                        onNavigateBack = { navController.popBackStack() },
                        onLaunchCalculator = { navController.navigate(AppRoutes.SOLAR_CALCULATOR) }
                    )
                }

                composable(AppRoutes.SETTINGS) {
                    SettingsScreen(
                        viewModel = viewModel,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onNavigateToAbout = { navController.navigate(AppRoutes.ABOUT) }
                    )
                }

                composable(AppRoutes.ABOUT) {
                    AboutScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
