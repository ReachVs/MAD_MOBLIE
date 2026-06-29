package com.example.mad_final.ui.screens.bookings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.mad_final.domain.models.Booking
import com.example.mad_final.domain.models.BookingStatus
import com.example.mad_final.ui.components.AppDrawerContent
import com.example.mad_final.ui.components.TechnicalGridBackground
import com.example.mad_final.ui.navigation.Screen
import com.example.mad_final.ui.theme.Neutral
import com.example.mad_final.ui.theme.Primary
import com.example.mad_final.ui.theme.Secondary
import com.example.mad_final.ui.theme.Info
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    onLogout: () -> Unit = {},
    onBack: () -> Unit,
    onHomeClick: () -> Unit = {},
    onCatalogClick: () -> Unit = {},
    onTrackingClick: () -> Unit = {},
    onBookClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: MyBookingsViewModel = hiltViewModel()
) {
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val userImageUri by viewModel.userImageUri.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val activeBookings = bookings.filter { 
        it.status != BookingStatus.COMPLETED && it.status != BookingStatus.CANCELLED 
    }
    val historyBookings = bookings.filter { 
        it.status == BookingStatus.COMPLETED || it.status == BookingStatus.CANCELLED 
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                currentRoute = Screen.MyBookings.route,
                userName = userName,
                userImageUri = userImageUri,
                onNavigate = { route ->
                    when (route) {
                        Screen.Dashboard.route -> onHomeClick()
                        Screen.Catalog.route -> onCatalogClick()
                        Screen.MyBookings.route -> scope.launch { drawerState.close() }
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
                MyBookingsTopBar(
                    onBack = onBack,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    userImageUri = userImageUri,
                    onProfileClick = onProfileClick
                )
            },
            bottomBar = {
                val hasActiveService = bookings.any { 
                    it.status != BookingStatus.CANCELLED && it.status != BookingStatus.COMPLETED 
                }
                com.example.mad_final.ui.components.MadApeBottomNavigation(
                    currentRoute = "tracking",
                    hasActiveService = hasActiveService,
                    onHomeClick = onHomeClick,
                    onCatalogClick = onCatalogClick,
                    onTrackingClick = {},
                    onBookClick = onBookClick
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
                                "SERVICE\nQUEUE",
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

                    if (activeBookings.isNotEmpty()) {
                        item {
                            SectionHeader(title = "ACTIVE SERVICES", count = activeBookings.size)
                        }

                        items(activeBookings) { booking ->
                            BookingTechnicalCard(
                                booking = booking
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    if (historyBookings.isNotEmpty()) {
                        item {
                            SectionHeader(title = "SERVICE HISTORY", count = historyBookings.size)
                        }

                        items(historyBookings) { booking ->
                            BookingTechnicalCard(
                                booking = booking
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    
                    if (bookings.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "NO SERVICE RECORDS FOUND",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Neutral.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, count: Int) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = Color.Black
            )
            Text(
                "[$count]",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Neutral
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(color = Color.Black, thickness = 2.dp)
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
                        BookingStatus.REPAIR -> Secondary
                        BookingStatus.WAITING_PART -> Secondary
                        BookingStatus.READY_TO_PICK_UP -> Primary
                        BookingStatus.CONFIRMED -> Primary
                        BookingStatus.PENDING -> Neutral
                        BookingStatus.COMPLETED -> Primary
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val machineName = when {
                            booking.customBrand != null && booking.customModel != null -> "${booking.customBrand} ${booking.customModel}".uppercase()
                            booking.motorcycleId == "custom_unit" -> "CUSTOM UNIT"
                            else -> booking.motorcycleId.uppercase()
                        }
                        Text("MACHINE UNIT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Neutral)
                        Text(
                            machineName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        val year = booking.customYear
                        if (!year.isNullOrBlank()) {
                            Text("YEAR: $year", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Neutral)
                        }
                    }
                }

                TechnicalDetailItem(
                    label = "TOTAL FEE",
                    value = "$${booking.totalPrice}",
                    isHighlight = true
                )
            }

            if (!booking.descriptionDetail.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "TECHNICAL NOTE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Neutral,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            booking.descriptionDetail,
                            fontSize = 11.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 16.sp
                        )
                    }
                }
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
                    Icons.Default.Person,
                    contentDescription = null,
                    size = 14.dp,
                    tint = Neutral
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "ASSIGNED: ${booking.technicianName.uppercase()}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Neutral
                )
                Spacer(modifier = Modifier.width(16.dp))
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
fun MyBookingsTopBar(
    onBack: () -> Unit,
    onMenuClick: () -> Unit = {},
    userImageUri: String? = null,
    onProfileClick: () -> Unit = {}
) {
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
            IconButton(onClick = onProfileClick) {
                val placeholderPainter = rememberVectorPainter(Icons.Default.Person)
                val errorPainter = rememberVectorPainter(Icons.Default.Person)
                
                AsyncImage(
                    model = userImageUri,
                    contentDescription = "User Profile",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.Black, CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = placeholderPainter,
                    error = errorPainter
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

