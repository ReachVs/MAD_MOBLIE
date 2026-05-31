package com.example.mad_final.ui.screens.bookings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mad_final.domain.models.Booking
import com.example.mad_final.domain.models.BookingStatus
import com.example.mad_final.ui.components.AppDrawerContent
import com.example.mad_final.ui.components.TechnicalGridBackground
import com.example.mad_final.ui.navigation.Screen
import com.example.mad_final.ui.theme.Neutral
import com.example.mad_final.ui.theme.Primary
import com.example.mad_final.ui.theme.Secondary
import com.example.mad_final.ui.theme.Success
import com.example.mad_final.ui.theme.Warning
import com.example.mad_final.ui.theme.Info
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    onLogout: () -> Unit = {},
    onBack: () -> Unit,
    onHomeClick: () -> Unit = {},
    onCatalogClick: () -> Unit = {},
    onBookClick: () -> Unit = {},
    onLiveFeedClick: () -> Unit = {},
    viewModel: MyBookingsViewModel = hiltViewModel()
) {
    val bookings by viewModel.bookings.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                currentRoute = Screen.MyBookings.route,
                onNavigate = { route ->
                    when (route) {
                        Screen.Dashboard.route -> onHomeClick()
                        Screen.Catalog.route -> onCatalogClick()
                        Screen.MyBookings.route -> scope.launch { drawerState.close() }
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
                MyBookingsTopBar(
                    onBack = onBack,
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            },
            bottomBar = {
                MyBookingsBottomNavigation(
                    onHomeClick = onHomeClick,
                    onCatalogClick = onCatalogClick,
                    onBookClick = onBookClick,
                    onProfileClick = {}
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
            ) {
                TechnicalGridBackground()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                "SERVICE\nHISTORY",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                lineHeight = 30.sp,
                                letterSpacing = (-1).sp
                            )
                            Text(
                                "RECORDS: ${bookings.size}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Neutral
                            )
                        }
                    }

                    items(bookings) { booking ->
                        BookingTechnicalCard(booking = booking)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun BookingTechnicalCard(booking: Booking) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(2.dp, Color.Black),
        color = Color.White,
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        "REFERENCE #${booking.id.take(8).uppercase()}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Neutral
                    )
                    Text(
                        "SERVICE ORDER",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                }

                Surface(
                    color = when (booking.status) {
                        BookingStatus.REPAIR -> Warning
                        BookingStatus.WAITING_PART -> Secondary
                        BookingStatus.READY_TO_PICK_UP -> Info
                        BookingStatus.CONFIRMED -> Primary
                        BookingStatus.PENDING -> Neutral
                        BookingStatus.COMPLETED -> Success
                        BookingStatus.CANCELLED -> Neutral.copy(alpha = 0.5f)
                    },
                    shape = RoundedCornerShape(0.dp)
                ) {
                    val statusTextColor =
                        if (booking.status != BookingStatus.PENDING) Color.White else Neutral
                    Text(
                        text = booking.status.name.replace("_", " "),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = statusTextColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            if (booking.workDescription.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = booking.workDescription,
                    fontSize = 12.sp,
                    color = Primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TechnicalDetailItem(label = "UNIT ID", value = booking.motorcycleId.take(12).uppercase())
                TechnicalDetailItem(
                    label = "TOTAL FEE",
                    value = "$${booking.totalPrice}",
                    isHighlight = true
                )
            }

            if (booking.serviceNotes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            "SERVICE NOTES",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Neutral
                        )
                        Text(booking.serviceNotes, fontSize = 11.sp, color = Primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = Color.Black.copy(alpha = 0.1f), thickness = 1.dp)

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                CustomIcon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    size = 14.dp,
                    tint = Neutral
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "LOGGED: ${
                        java.text.SimpleDateFormat(
                            "MMM dd, yyyy",
                            java.util.Locale.US
                        ).format(java.util.Date(booking.startDate))
                    }",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Neutral
                )
            }
        }
    }
}

@Composable
fun TechnicalDetailItem(label: String, value: String, isHighlight: Boolean = false) {
    Column {
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Neutral)
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = if (isHighlight) Secondary else Primary
        )
    }
}

@Composable
fun CustomIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String?,
    size: androidx.compose.ui.unit.Dp,
    tint: Color
) {
    androidx.compose.material3.Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = Modifier.size(size),
        tint = tint
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsTopBar(onBack: () -> Unit, onMenuClick: () -> Unit = {}) {
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
            Row {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            IconButton(onClick = {}) {
                val placeholderPainter = rememberVectorPainter(Icons.Default.Refresh)
                val errorPainter = rememberVectorPainter(Icons.Default.Warning)
                
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?q=80&w=100&auto=format&fit=crop",
                    contentDescription = "User Profile",
                    modifier = Modifier
                        .size(32.dp)
                        .border(1.dp, Color.Black),
                    contentScale = ContentScale.Crop,
                    placeholder = placeholderPainter,
                    error = errorPainter,
                    onLoading = { android.util.Log.d("MyBookings", "Loading profile image") },
                    onError = { android.util.Log.e("MyBookings", "Error loading profile image", it.result.throwable) }
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
fun MyBookingsBottomNavigation(
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
            selected = false,
            onClick = onHomeClick,
            colors = NavigationBarItemDefaults.colors(
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
            selected = true,
            onClick = onProfileClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Secondary,
                selectedTextColor = Secondary,
                unselectedIconColor = Neutral,
                unselectedTextColor = Neutral,
                indicatorColor = Color.Transparent
            )
        )
    }
}
