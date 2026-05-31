package com.example.mad_final.ui.screens.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mad_final.ui.components.AppDrawerContent
import com.example.mad_final.ui.components.TechnicalGridBackground
import com.example.mad_final.ui.navigation.Screen
import com.example.mad_final.domain.models.Booking
import com.example.mad_final.domain.models.Motorcycle
import com.example.mad_final.domain.models.BookingStatus

import com.example.mad_final.ui.theme.Primary
import com.example.mad_final.ui.theme.Secondary
import com.example.mad_final.ui.theme.Neutral
import com.example.mad_final.ui.theme.Background
import com.example.mad_final.ui.theme.Success
import com.example.mad_final.ui.theme.Warning
import com.example.mad_final.ui.theme.Info
import com.example.mad_final.ui.theme.LightGrid

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    MaterialTheme {
        DashboardContent(
            bookings = emptyList(),
            vehicles = emptyList()
        )
    }
}

@Composable
fun DashboardScreen(
    onLogout: () -> Unit = {},
    onExploreClick: () -> Unit = {},
    onMyBookingsClick: () -> Unit = {},
    onAdminClick: () -> Unit = {},
    onBookClick: () -> Unit = {},
    onLiveFeedClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val bookings by viewModel.bookings.collectAsState()
    val motorcycles by viewModel.motorcycles.collectAsState()

    DashboardContent(
        bookings = bookings,
        vehicles = motorcycles,
        onLogout = onLogout,
        onExploreClick = onExploreClick,
        onMyBookingsClick = onMyBookingsClick,
        onAdminClick = onAdminClick,
        onBookClick = onBookClick,
        onLiveFeedClick = onLiveFeedClick,
        onProfileClick = onProfileClick
    )
}

@Composable
fun DashboardContent(
    bookings: List<Booking>,
    vehicles: List<Motorcycle>,
    onLogout: () -> Unit = {},
    onExploreClick: () -> Unit = {},
    onMyBookingsClick: () -> Unit = {},
    onAdminClick: () -> Unit = {},
    onBookClick: () -> Unit = {},
    onLiveFeedClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                currentRoute = Screen.Dashboard.route,
                onNavigate = { route ->
                    when (route) {
                        Screen.Dashboard.route -> scope.launch { drawerState.close() } // Already here
                        Screen.Catalog.route -> onExploreClick()
                        Screen.MyBookings.route -> onMyBookingsClick()
                        Screen.LiveFeed.route -> onLiveFeedClick()
                        Screen.Booking.createRoute("custom_unit") -> onBookClick()
                        else -> scope.launch { drawerState.close() }
                    }
                },
                onLogout = onLogout,
                onClose = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = { 
                DashboardTopBar(
                    onProfileClick = onProfileClick,
                    onMenuClick = { scope.launch { drawerState.open() } }
                ) 
            },
            bottomBar = { 
            DashboardBottomNavigation(
                onHomeClick = {},
                onCatalogClick = onExploreClick,
                onBookClick = onBookClick,
                onProfileClick = onProfileClick
            ) 
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Open Chat */ },
                containerColor = Primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
            ) {
                Icon(Icons.Default.Email, contentDescription = "Chat")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Background)
        ) {
            TechnicalGridBackground()
            
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item { 
                    val activeBooking = bookings.filter { 
                        it.status != BookingStatus.CANCELLED 
                    }.maxByOrNull { it.startDate }

                    ActiveServiceSection(
                        booking = activeBooking,
                        onLiveFeedClick = onLiveFeedClick
                    ) 
                }
                item { 
                    MyVehicleSection(
                        vehicles = vehicles, 
                        bookings = bookings,
                        onManageVehiclesClick = onProfileClick,
                        onTrackingClick = onMyBookingsClick
                    ) 
                }
            }
        }
    }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(onProfileClick: () -> Unit, onMenuClick: () -> Unit = {}) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "MAD APE",
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontSize = 20.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(onClick = onProfileClick) {
                val placeholderPainter = rememberVectorPainter(Icons.Default.Refresh)
                val errorPainter = rememberVectorPainter(Icons.Default.Warning)
                
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?q=80&w=100&auto=format&fit=crop",
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(32.dp)
                        .border(1.dp, Color.Black),
                    contentScale = ContentScale.Crop,
                    placeholder = placeholderPainter,
                    error = errorPainter,
                    onLoading = { android.util.Log.d("DashboardScreen", "Loading profile image") },
                    onError = { android.util.Log.e("DashboardScreen", "Error loading profile image", it.result.throwable) }
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
        ),
        modifier = Modifier.drawBehind {
            drawLine(
                color = Color.Black,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Square
            )
        }
    )
}

@Composable
fun ActiveServiceSection(booking: Booking?, onLiveFeedClick: () -> Unit) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            "ACTIVE SERVICE",
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp,
            color = Neutral
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(2.dp, Color.Black),
            color = Color.White,
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                if (booking != null) {
                    val statusText = when (booking.status) {
                        BookingStatus.REPAIR -> "MAINTENANCE"
                        BookingStatus.WAITING_PART -> "AWAITING PARTS"
                        BookingStatus.READY_TO_PICK_UP -> "READY"
                        BookingStatus.PENDING -> "QUEUED"
                        BookingStatus.CONFIRMED -> "CONFIRMED"
                        BookingStatus.COMPLETED -> "COMPLETED"
                        else -> "PROCESSING"
                    }

                    val statusMessage = when (booking.status) {
                        BookingStatus.REPAIR -> "Our specialists are currently performing maintenance on your machine."
                        BookingStatus.WAITING_PART -> "Awaiting high-performance components for your machine's optimization."
                        BookingStatus.READY_TO_PICK_UP -> "System verification complete. Your machine is ready for collection."
                        BookingStatus.COMPLETED -> "Service successfully completed. Machine is at peak performance."
                        else -> "Your unit is currently in the queue for technical inspection."
                    }

                    Surface(
                        color = LightGrid,
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Text(
                            statusText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Neutral,
                            letterSpacing = 0.5.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        "ORDER #${booking.id.take(8).uppercase()}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        statusMessage,
                        fontSize = 12.sp,
                        color = Neutral,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 18.sp
                    )
                } else {
                    Text(
                        "NO ACTIVE SERVICE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Neutral
                    )
                    Text(
                        "READY FOR NEXT RIDE",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Book your next maintenance to keep performance optimal.",
                        fontSize = 12.sp,
                        color = Neutral,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onLiveFeedClick,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(0.dp),
                    border = BorderStroke(2.dp, Color.Black)
                ) {
                    Text("LIVE WORKSHOP FEED", fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 1.sp)
                }
            }
        }
    }
}


@Composable
fun MyVehicleSection(
    vehicles: List<Motorcycle>, 
    bookings: List<Booking>,
    onManageVehiclesClick: () -> Unit,
    onTrackingClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                "MY VEHICLE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = Color.Black
            )
            Text(
                "MANAGE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = Secondary,
                modifier = Modifier.clickable { onManageVehiclesClick() }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (vehicles.isEmpty()) {
            VehicleServiceCard(
                vehicle = Motorcycle(
                    id = "NULL_UNIT", 
                    brand = "MAD APE", 
                    model = "NO UNITS DETECTED", 
                    year = 0, 
                    pricePerDay = 0.0, 
                    availability = false, 
                    imageUrl = "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?q=80&w=400&auto=format&fit=crop", 
                    description = "", 
                    type = "NONE"
                ),
                latestBooking = null,
                onClick = {}
            )
        } else {
            vehicles.forEach { vehicle ->
                val latestBooking = bookings
                    .filter { it.motorcycleId == vehicle.id }
                    .maxByOrNull { it.startDate }

                val isActive = latestBooking != null && 
                    latestBooking.status != BookingStatus.COMPLETED && 
                    latestBooking.status != BookingStatus.CANCELLED

                VehicleServiceCard(
                    vehicle = vehicle,
                    latestBooking = latestBooking,
                    onClick = { if (isActive) onTrackingClick() else onManageVehiclesClick() }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun VehicleServiceCard(
    vehicle: Motorcycle,
    latestBooking: Booking?,
    onClick: () -> Unit
) {
    val statusText = when (latestBooking?.status) {
        BookingStatus.REPAIR -> "MAINTENANCE"
        BookingStatus.WAITING_PART -> "AWAITING PARTS"
        BookingStatus.READY_TO_PICK_UP -> "READY"
        BookingStatus.PENDING, 
        BookingStatus.CONFIRMED -> "QUEUED"
        BookingStatus.COMPLETED -> "COMPLETED"
        else -> "NOMINAL"
    }

    val statusColor = when(latestBooking?.status) {
        BookingStatus.REPAIR -> Warning
        BookingStatus.WAITING_PART -> Secondary
        BookingStatus.READY_TO_PICK_UP -> Info
        BookingStatus.CONFIRMED -> Primary
        BookingStatus.PENDING -> Primary
        BookingStatus.COMPLETED -> Success
        else -> LightGrid
    }

    val statusTextColor = if (latestBooking != null && 
        latestBooking.status != BookingStatus.PENDING) Color.White else Neutral

    val context = androidx.compose.ui.platform.LocalContext.current
    val imageModel = remember(vehicle.imageUrl) {
        val url = vehicle.imageUrl.trim()
        if (url.startsWith("http")) {
            url
        } else if (url.isNotEmpty()) {
            val resId = context.resources.getIdentifier(url, "drawable", context.packageName)
            if (resId != 0) resId else null
        } else {
            null
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = Color.White,
        border = BorderStroke(2.dp, Color.Black),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Vehicle Image (Picture Logo Style)
            val placeholderPainter = rememberVectorPainter(Icons.Default.Refresh)
            val errorPainter = rememberVectorPainter(Icons.Default.Warning)

            AsyncImage(
                model = imageModel,
                contentDescription = "Image for ${vehicle.brand} ${vehicle.model}",
                modifier = Modifier
                    .size(95.dp)
                    .border(1.dp, Color.Black)
                    .background(Color.White),
                contentScale = ContentScale.Crop,
                placeholder = placeholderPainter,
                error = errorPainter,
                onLoading = { android.util.Log.d("DashboardScreen", "Loading image: $imageModel") },
                onError = { android.util.Log.e("DashboardScreen", "Error loading image: $imageModel", it.result.throwable) }
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                // Header: Ref # and Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            latestBooking?.let { "REF: ${it.id.take(8).uppercase()}" } ?: "SYSTEM READY",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Neutral
                        )
                        Text(
                            "SERVICE ORDER",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )
                    }
                    
                    Surface(
                        color = statusColor,
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Text(
                            statusText,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = statusTextColor,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Vehicle Identity
                Text(
                    "${vehicle.brand} ${vehicle.model}".uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = Primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Description / Work
                if (latestBooking?.workDescription?.isNotEmpty() == true) {
                    Text(
                        latestBooking.workDescription,
                        fontSize = 10.sp,
                        color = Neutral,
                        maxLines = 1,
                        lineHeight = 12.sp,
                        fontWeight = FontWeight.Medium,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                
                HorizontalDivider(color = Color.Black.copy(alpha = 0.1f), thickness = 1.dp)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Footer Details: Date and Unit ID
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Settings, 
                            contentDescription = null, 
                            modifier = Modifier.size(10.dp),
                            tint = Neutral
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "ID: ${vehicle.id.take(8).uppercase()}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Neutral
                        )
                    }
                    
                    if (latestBooking != null) {
                        Text(
                            java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.US)
                                .format(java.util.Date(latestBooking.startDate)),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Neutral
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight, 
                contentDescription = null,
                tint = Secondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


@Composable
fun DashboardBottomNavigation(
    onHomeClick: () -> Unit,
    onCatalogClick: () -> Unit,
    onBookClick: () -> Unit,
    onProfileClick: () -> Unit
) {
        NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier.drawBehind {
            drawLine(
                color = Color.Black,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 2.dp.toPx()
            )
        }
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("HOME", fontSize = 10.sp, fontWeight = FontWeight.Black) },
            selected = true,
            onClick = onHomeClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Secondary,
                selectedTextColor = Secondary,
                unselectedIconColor = Neutral,
                unselectedTextColor = Neutral,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
            label = { Text("CATALOG", fontSize = 10.sp, fontWeight = FontWeight.Black) },
            selected = false,
            onClick = onCatalogClick,
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Neutral,
                unselectedTextColor = Neutral,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AddCircle, contentDescription = null) },
            label = { Text("BOOK", fontSize = 10.sp, fontWeight = FontWeight.Black) },
            selected = false,
            onClick = onBookClick,
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Neutral,
                unselectedTextColor = Neutral,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("PROFILE", fontSize = 10.sp, fontWeight = FontWeight.Black) },
            selected = false,
            onClick = onProfileClick,
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Neutral,
                unselectedTextColor = Neutral,
                indicatorColor = Color.Transparent
            )
        )
    }
}
