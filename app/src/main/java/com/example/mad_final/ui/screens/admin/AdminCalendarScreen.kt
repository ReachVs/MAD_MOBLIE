package com.example.mad_final.ui.screens.admin

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mad_final.domain.models.BookingStatus
import com.example.mad_final.domain.models.Priority
import com.example.mad_final.ui.components.TechnicalGridBackground
import com.example.mad_final.ui.navigation.Screen
import com.example.mad_final.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCalendarScreen(
    onLogout: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onInventoryClick: () -> Unit = {},
    onQueueClick: () -> Unit = {},
    onRevenueClick: () -> Unit = {},
    onServicesClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val adminImageUri by viewModel.adminImageUri.collectAsStateWithLifecycle()
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    
    val selectedDate = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
    
    val bookingsForSelectedDate = remember(selectedDate, bookings) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = selectedDate
        val day = cal.get(Calendar.DAY_OF_YEAR)
        val year = cal.get(Calendar.YEAR)
        
        bookings.filter {
            val bCal = Calendar.getInstance()
            bCal.timeInMillis = it.startDate
            bCal.get(Calendar.DAY_OF_YEAR) == day && bCal.get(Calendar.YEAR) == year
        }.sortedBy { it.startDate }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AdminDrawerContent(
                onDashboardClick = onHomeClick,
                onInventoryClick = onInventoryClick,
                onQueueClick = onQueueClick,
                onRevenueClick = onRevenueClick,
                onCalendarClick = { scope.launch { drawerState.close() } },
                onServicesClick = {
                    scope.launch { drawerState.close() }
                    onServicesClick()
                },
                onLogout = onLogout,
                onClose = { scope.launch { drawerState.close() } },
                currentRoute = Screen.AdminCalendar.route,
                userName = userName,
                userImageUri = adminImageUri
            )
        }
    ) {
        Scaffold(
            topBar = {
                AdminTopBar(
                    title = "OPERATIONAL CALENDAR",
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onProfileClick = onProfileClick,
                    userImageUri = adminImageUri
                )
            },
            bottomBar = {
                AdminBottomNavigation(
                    onDashboardClick = onHomeClick,
                    onInventoryClick = onInventoryClick,
                    onQueueClick = onQueueClick,
                    currentRoute = Screen.AdminCalendar.route
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                TechnicalGridBackground()
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Calendar Section
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(2.dp, Color.Black),
                        color = Color.White,
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            DatePicker(
                                state = datePickerState,
                                title = null,
                                headline = null,
                                showModeToggle = false,
                                colors = DatePickerDefaults.colors(
                                    containerColor = Color.White,
                                    selectedDayContainerColor = Secondary,
                                    selectedDayContentColor = Color.White,
                                    todayContentColor = Secondary,
                                    todayDateBorderColor = Secondary
                                ),
                                modifier = Modifier.scale(0.9f)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        "SCHEDULE FOR ${SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(Date(selectedDate)).uppercase()}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Neutral,
                        letterSpacing = 1.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (bookingsForSelectedDate.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "NO WORK ORDERS SCHEDULED",
                                fontWeight = FontWeight.Black,
                                color = Neutral.copy(alpha = 0.5f)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(bookingsForSelectedDate) { booking ->
                                CalendarBookingCard(booking)
                            }
                            item { Spacer(modifier = Modifier.height(32.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarBookingCard(booking: com.example.mad_final.domain.models.Booking) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(2.dp, Color.Black),
        color = Color.White,
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val time = SimpleDateFormat("hh:mm a", Locale.US).format(Date(booking.startDate))
            
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(70.dp)) {
                Text(
                    time.split(" ")[0],
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    time.split(" ")[1],
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Neutral
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Box(modifier = Modifier.width(2.dp).height(40.dp).background(Color.Black))
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    booking.workDescription.uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1
                )
                Text(
                    "UNIT: ${booking.motorcycleId.take(8).uppercase()}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Neutral
                )
            }
            
            Surface(
                color = when(booking.status) {
                    BookingStatus.REPAIR -> Secondary
                    BookingStatus.WAITING_PART -> Secondary
                    BookingStatus.READY_TO_PICK_UP -> Primary
                    BookingStatus.CONFIRMED -> Primary
                    BookingStatus.PENDING -> Color.LightGray
                    BookingStatus.COMPLETED -> Primary
                    else -> Neutral
                },
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black)
            ) {
                Text(
                    booking.status.name.take(1),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
