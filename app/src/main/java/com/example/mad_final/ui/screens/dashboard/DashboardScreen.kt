package com.example.mad_final.ui.screens.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.mad_final.ui.components.*
import com.example.mad_final.ui.navigation.Screen
import com.example.mad_final.domain.models.Booking
import com.example.mad_final.domain.models.BookingStatus
import com.example.mad_final.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    onLogout: () -> Unit = {},
    onExploreClick: () -> Unit = {},
    onTrackingClick: () -> Unit = {},
    onBookClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userImageUri by viewModel.userImageUri.collectAsStateWithLifecycle()
    val userRole by viewModel.userRole.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                currentRoute = Screen.Dashboard.route,
                userName = userName,
                userImageUri = userImageUri,
                onNavigate = { route ->
                    when (route) {
                        Screen.Dashboard.route -> scope.launch { drawerState.close() }
                        Screen.Catalog.route -> onExploreClick()
                        Screen.MyBookings.route -> onTrackingClick()
                        Screen.Booking.createRoute("custom_unit") -> onBookClick()
                        Screen.Profile.route -> onProfileClick()
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
                    userImageUri = userImageUri,
                    onProfileClick = onProfileClick,
                    onMenuClick = { scope.launch { drawerState.open() } }
                ) 
            },
            bottomBar = { 
                val hasActiveService = bookings.any { 
                    it.status != BookingStatus.CANCELLED && it.status != BookingStatus.COMPLETED 
                }
                MadApeBottomNavigation(
                    currentRoute = "home",
                    hasActiveService = hasActiveService,
                    onHomeClick = {},
                    onCatalogClick = onExploreClick,
                    onTrackingClick = onTrackingClick,
                    onBookClick = onBookClick
                ) 
            },
            floatingActionButton = {
                if (userRole != "CUSTOMER") {
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
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(Background)) {
                TechnicalGridBackground()
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { HeroSection(onActionClick = onBookClick) }
                    item { BrandStatsSection() }
                    item {
                        val activeBooking = bookings.filter { 
                            it.status != BookingStatus.CANCELLED && it.status != BookingStatus.COMPLETED
                        }.maxByOrNull { it.startDate }

                        ActiveServiceSection(booking = activeBooking, onTrackClick = onTrackingClick)
                    }
                    item { FeaturedServicesSection(onCatalogClick = onExploreClick) }
                    item { ContactSection() }
                    item { BrandFooterSection() }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(
    userImageUri: String?,
    onProfileClick: () -> Unit, 
    onMenuClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text("MAD APE", fontWeight = FontWeight.Black, letterSpacing = 2.sp, fontSize = 20.sp)
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(onClick = onProfileClick) {
                val placeholderPainter = rememberVectorPainter(Icons.Default.Person)
                val errorPainter = rememberVectorPainter(Icons.Default.Warning)
                AsyncImage(
                    model = userImageUri,
                    contentDescription = "Profile",
                    modifier = Modifier.size(32.dp).border(1.dp, Color.Black),
                    contentScale = ContentScale.Crop,
                    placeholder = placeholderPainter,
                    error = errorPainter
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
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
fun ActiveServiceSection(booking: Booking?, onTrackClick: () -> Unit = {}) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text("ACTIVE SERVICE", fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp, color = Neutral)
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            modifier = Modifier.fillMaxWidth().clickable { onTrackClick() },
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

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = LightGrid, shape = RoundedCornerShape(0.dp)) {
                            Text(statusText, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 9.sp, fontWeight = FontWeight.Black, color = Neutral, letterSpacing = 0.5.sp)
                        }
                        Text("ID: ${booking.id.take(8).uppercase()}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Neutral)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("SERVICE DETAIL", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Secondary, letterSpacing = 1.sp)
                    Text(booking.workDescription.ifEmpty { "GENERAL MAINTENANCE" }.uppercase(), fontSize = 18.sp, fontWeight = FontWeight.Black, lineHeight = 22.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("MACHINE UNIT", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Neutral)
                        val machineName = when {
                            booking.customBrand != null && booking.customModel != null -> "${booking.customBrand} ${booking.customModel}".uppercase()
                            booking.motorcycleId == "custom_unit" -> "CUSTOM UNIT"
                            else -> booking.motorcycleId.uppercase()
                        }
                        Text(machineName, fontSize = 13.sp, fontWeight = FontWeight.Black, color = Primary)
                        booking.customYear?.let { if (it.isNotBlank()) Text("YEAR: $it", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Neutral) }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ASSIGNED SPECIALIST", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Neutral)
                            Text(booking.technicianName.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("SCHEDULED", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Neutral)
                            val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(booking.startDate))
                            val timeStr = SimpleDateFormat("hh:mm a", Locale.US).format(Date(booking.startDate))
                            Text("$dateStr\n$timeStr", fontSize = 12.sp, fontWeight = FontWeight.Black, lineHeight = 14.sp)
                        }
                    }
                    booking.descriptionDetail?.let { if (it.isNotBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(color = Color(0xFFF1F5F9), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(0.dp)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("TECHNICAL NOTE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Neutral, letterSpacing = 1.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(it, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black, lineHeight = 16.sp)
                            }
                        }
                    }}
                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = Color.Black.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(statusMessage, fontSize = 11.sp, color = Neutral, fontWeight = FontWeight.Bold, lineHeight = 16.sp)
                } else {
                    Text("NO ACTIVE SERVICE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Neutral)
                    Text("READY FOR NEXT RIDE", fontSize = 24.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Book your next maintenance to keep performance optimal.", fontSize = 12.sp, color = Neutral, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    MaterialTheme {
        DashboardScreen()
    }
}
