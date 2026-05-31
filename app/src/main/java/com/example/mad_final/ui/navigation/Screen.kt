package com.example.mad_final.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Catalog : Screen("catalog")
    object ServiceDetail : Screen("service_detail/{serviceId}") {
        fun createRoute(serviceId: String) = "service_detail/$serviceId"
    }
    object MotorcycleDetail : Screen("motorcycle_detail/{motorcycleId}") {
        fun createRoute(motorcycleId: String) = "motorcycle_detail/$motorcycleId"
    }
    object Booking : Screen("booking/{motorcycleId}?config={config}") {
        fun createRoute(motorcycleId: String, config: String? = null) = 
            if (config != null) "booking/$motorcycleId?config=$config" else "booking/$motorcycleId"
    }
    object MyBookings : Screen("my_bookings")
    object AdminInventory : Screen("admin_inventory")
    object AdminQueue : Screen("admin_queue")
    object AdminDashboard : Screen("admin_dashboard")
    object AdminRevenue : Screen("admin_revenue")
    object LiveFeed : Screen("live_feed")
    object Landing : Screen("landing")
    object Splash : Screen("splash")
}
