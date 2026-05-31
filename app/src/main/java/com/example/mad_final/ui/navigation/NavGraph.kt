package com.example.mad_final.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.mad_final.ui.screens.auth.SessionState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mad_final.ui.screens.auth.LoginScreen
import com.example.mad_final.ui.screens.auth.RegisterScreen
import com.example.mad_final.ui.screens.admin.AdminInventoryScreen
import com.example.mad_final.ui.screens.admin.AdminQueueScreen
import com.example.mad_final.ui.screens.admin.AdminRevenueScreen
import com.example.mad_final.ui.screens.admin.AdminDashboardScreen
import com.example.mad_final.ui.screens.booking.BookingScreen
import com.example.mad_final.ui.screens.bookings.MyBookingsScreen
import com.example.mad_final.ui.screens.catalog.CatalogScreen
import com.example.mad_final.ui.screens.dashboard.DashboardScreen
import com.example.mad_final.ui.screens.dashboard.LiveWorkshopFeedScreen
import com.example.mad_final.ui.screens.detail.MotorcycleDetailScreen
import com.example.mad_final.ui.screens.detail.ServiceDetailScreen
import com.example.mad_final.ui.screens.landing.LandingScreen
import com.example.mad_final.ui.screens.landing.SplashScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    authViewModel: com.example.mad_final.ui.screens.auth.AuthViewModel = hiltViewModel()
) {
    val sessionState by authViewModel.sessionState.collectAsState()

    // GLOBAL AUTH GUARD: If unauthenticated, force to Landing page instead of Dashboard
    LaunchedEffect(sessionState) {
        if (sessionState is SessionState.Unauthenticated) {
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (currentRoute != Screen.Login.route && 
                currentRoute != Screen.Register.route &&
                currentRoute != Screen.Landing.route &&
                currentRoute != Screen.Splash.route) {
                navController.navigate(Screen.Landing.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onNavigateToAdmin = {
                    navController.navigate(Screen.AdminDashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToCustomer = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLanding = {
                    navController.navigate(Screen.Landing.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.Landing.route) {
            val sessionState by authViewModel.sessionState.collectAsState()
            LandingScreen(
                onBookServiceClick = {
                    if (sessionState is SessionState.Authenticated) {
                        val isAdmin = (sessionState as SessionState.Authenticated).role == "ADMIN"
                        if (isAdmin) navController.navigate(Screen.AdminDashboard.route)
                        else navController.navigate(Screen.Dashboard.route)
                    } else {
                        navController.navigate(Screen.Login.route)
                    }
                },
                onProfileClick = {
                    if (sessionState is SessionState.Authenticated) {
                        val isAdmin = (sessionState as SessionState.Authenticated).role == "ADMIN"
                        if (isAdmin) navController.navigate(Screen.AdminDashboard.route)
                        else navController.navigate(Screen.Dashboard.route)
                    } else {
                        navController.navigate(Screen.Login.route)
                    }
                },
                onLoginClick = {
                    if (sessionState is SessionState.Authenticated) {
                        val isAdmin = (sessionState as SessionState.Authenticated).role == "ADMIN"
                        if (isAdmin) navController.navigate(Screen.AdminDashboard.route)
                        else navController.navigate(Screen.Dashboard.route)
                    } else {
                        navController.navigate(Screen.Login.route)
                    }
                }
            )
        }
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { isAdmin ->
                    if (isAdmin) {
                        navController.navigate(Screen.AdminDashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(route = Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = Screen.Dashboard.route) {
            DashboardScreen(
                onLogout = {
                    authViewModel.logout()
                },
                onExploreClick = {
                    navController.navigate(Screen.Catalog.route)
                },
                onMyBookingsClick = {
                    navController.navigate(Screen.MyBookings.route)
                },
                onBookClick = {
                    navController.navigate(Screen.Booking.createRoute("custom_unit"))
                },
                onLiveFeedClick = {
                    navController.navigate(Screen.LiveFeed.route)
                },
                onProfileClick = {
                    navController.navigate(Screen.MyBookings.route)
                }
            )
        }
        composable(route = Screen.LiveFeed.route) {
            LiveWorkshopFeedScreen(
                onBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    authViewModel.logout()
                },
                onDashboardClick = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onExploreClick = {
                    navController.navigate(Screen.Catalog.route)
                },
                onMyBookingsClick = {
                    navController.navigate(Screen.MyBookings.route)
                },
                onBookClick = {
                    navController.navigate(Screen.Booking.createRoute("custom_unit"))
                }
            )
        }
        composable(route = Screen.Catalog.route) {
            CatalogScreen(
                onLogout = {
                    authViewModel.logout()
                },
                onServiceClick = { id ->
                    navController.navigate(Screen.ServiceDetail.createRoute(id))
                },
                onHomeClick = {
                    navController.navigate(Screen.Dashboard.route)
                },
                onLiveFeedClick = {
                    navController.navigate(Screen.LiveFeed.route)
                },
                onBookClick = {
                    navController.navigate(Screen.Booking.createRoute("custom_unit"))
                },
                onProfileClick = {
                    navController.navigate(Screen.MyBookings.route)
                }
            )
        }
        composable(route = Screen.ServiceDetail.route) {
            ServiceDetailScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onBookClick = { id, config ->
                    navController.navigate(Screen.Booking.createRoute(id, config))
                }
            )
        }
        composable(route = Screen.MotorcycleDetail.route) {
            MotorcycleDetailScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onBookClick = { id ->
                    navController.navigate(Screen.Booking.createRoute(id))
                }
            )
        }
        composable(
            route = Screen.Booking.route,
            arguments = listOf(
                androidx.navigation.navArgument("config") { 
                    type = androidx.navigation.NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            BookingScreen(
                onBookingFinished = {
                    navController.navigate(Screen.MyBookings.route) {
                        popUpTo(Screen.Dashboard.route)
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(route = Screen.MyBookings.route) {
            MyBookingsScreen(
                onLogout = {
                    authViewModel.logout()
                },
                onBack = {
                    navController.popBackStack()
                },
                onHomeClick = {
                    navController.navigate(Screen.Dashboard.route)
                },
                onCatalogClick = {
                    navController.navigate(Screen.Catalog.route)
                },
                onBookClick = {
                    navController.navigate(Screen.Booking.createRoute("custom_unit"))
                },
                onLiveFeedClick = {
                    navController.navigate(Screen.LiveFeed.route)
                }
            )
        }
        composable(route = Screen.AdminInventory.route) {
            AdminInventoryScreen(
                onLogout = {
                    authViewModel.logout()
                },
                onHomeClick = {
                    navController.navigate(Screen.AdminDashboard.route)
                },
                onQueueClick = {
                    navController.navigate(Screen.AdminQueue.route)
                },
                onRevenueClick = {
                    navController.navigate(Screen.AdminRevenue.route)
                }
            )
        }
        composable(route = Screen.AdminQueue.route) {
            AdminQueueScreen(
                onLogout = {
                    authViewModel.logout()
                },
                onHomeClick = {
                    navController.navigate(Screen.AdminDashboard.route)
                },
                onInventoryClick = {
                    navController.navigate(Screen.AdminInventory.route)
                },
                onRevenueClick = {
                    navController.navigate(Screen.AdminRevenue.route)
                }
            )
        }
        composable(route = Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onLogout = {
                    authViewModel.logout()
                },
                onHomeClick = {
                    // Already here
                },
                onInventoryClick = {
                    navController.navigate(Screen.AdminInventory.route)
                },
                onQueueClick = {
                    navController.navigate(Screen.AdminQueue.route)
                },
                onRevenueClick = {
                    navController.navigate(Screen.AdminRevenue.route)
                }
            )
        }
        composable(route = Screen.AdminRevenue.route) {
            AdminRevenueScreen(
                onLogout = {
                    authViewModel.logout()
                },
                onHomeClick = {
                    navController.navigate(Screen.AdminDashboard.route)
                },
                onInventoryClick = {
                    navController.navigate(Screen.AdminInventory.route)
                },
                onQueueClick = {
                    navController.navigate(Screen.AdminQueue.route)
                }
            )
        }
    }
}
