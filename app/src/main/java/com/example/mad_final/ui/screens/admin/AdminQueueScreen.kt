package com.example.mad_final.ui.screens.admin

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mad_final.ui.navigation.Screen
import com.example.mad_final.ui.theme.Primary
import com.example.mad_final.ui.theme.Secondary
import com.example.mad_final.ui.theme.Neutral
import com.example.mad_final.ui.theme.Warning
import com.example.mad_final.ui.theme.Info
import com.example.mad_final.domain.models.Booking
import com.example.mad_final.domain.models.BookingStatus
import com.example.mad_final.domain.models.Priority
import java.util.Locale
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mad_final.ui.components.TechnicalGridBackground

@Preview(showBackground = true)
@Composable
fun AdminQueueScreenPreview() {
    MaterialTheme {
        AdminQueueScreen()
    }
}

@Composable
fun AdminQueueScreen(
    onLogout: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onInventoryClick: () -> Unit = {},
    onRevenueClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    onServicesClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: AdminQueueViewModel = hiltViewModel()
) {
    val filteredBookings by viewModel.filteredBookings.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val motorcycles by viewModel.motorcycles.collectAsStateWithLifecycle()
    val availableParts by viewModel.availableParts.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val adminImageUri by viewModel.adminImageUri.collectAsStateWithLifecycle()
    val tabs = listOf("ALL ORDERS", "IN SERVICE", "HISTORY")

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AdminDrawerContent(
                onDashboardClick = onHomeClick,
                onInventoryClick = onInventoryClick,
                onQueueClick = { scope.launch { drawerState.close() } },
                onRevenueClick = {
                    scope.launch { drawerState.close() }
                    onRevenueClick()
                },
                onCalendarClick = {
                    scope.launch { drawerState.close() }
                    onCalendarClick()
                },
                onServicesClick = {
                    scope.launch { drawerState.close() }
                    onServicesClick()
                },
                onProfileClick = {
                    scope.launch { drawerState.close() }
                    onProfileClick()
                },
                onLogout = onLogout,
                onClose = {
                    scope.launch { drawerState.close() }
                },
                currentRoute = Screen.AdminQueue.route,
                userName = userName,
                userImageUri = adminImageUri
            )
        }
    ) {
        Scaffold(
            topBar = { 
                AdminTopBar(
                    title = "WORK ORDERS",
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                    onProfileClick = onProfileClick,
                    onCalendarClick = onCalendarClick,
                    showSearch = true,
                    userImageUri = adminImageUri
                ) 
            },
            bottomBar = {
                AdminBottomNavigation(
                    onDashboardClick = onHomeClick,
                    onInventoryClick = onInventoryClick,
                    onQueueClick = {},
                    currentRoute = Screen.AdminQueue.route
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
                ) {
                    // Tab Selector
                    QueueTabSelector(
                        selectedTab = selectedTab,
                        tabs = tabs,
                        onTabSelect = viewModel::setSelectedTab
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                    ) {
                        item {
                            Text(
                                "ENGINEERING QUEUE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = Neutral,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        items(filteredBookings) { booking ->
                            QueueItemCard(
                                booking = booking,
                                motorcycles = motorcycles,
                                availableParts = availableParts,
                                onStatusChange = { newStatus ->
                                    viewModel.updateBookingStatus(booking.copy(status = newStatus))
                                },
                                onDetailsChange = { notes, desc, tech, priority, parts ->
                                    viewModel.updateBookingStatus(
                                        booking.copy(
                                            serviceNotes = notes,
                                            workDescription = desc,
                                            technicianName = tech,
                                            priority = priority,
                                            usedPartIds = parts
                                        )
                                    )
                                },
                                onDeleteClick = {
                                    viewModel.deleteBooking(booking.id)
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        item { Spacer(modifier = Modifier.height(32.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun QueueTabSelector(
    selectedTab: Int,
    tabs: List<String>,
    onTabSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .background(if (selectedTab == index) Primary else Color.White)
                    .clickable { onTabSelect(index) }
                    .border(2.dp, Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    title,
                    color = if (selectedTab == index) Color.White else Neutral,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
            if (index < tabs.size - 1) Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Composable
fun QueueItemCard(
    booking: Booking,
    motorcycles: List<com.example.mad_final.domain.models.Motorcycle> = emptyList(),
    availableParts: List<com.example.mad_final.domain.models.Part> = emptyList(),
    onStatusChange: (BookingStatus) -> Unit = {},
    onDetailsChange: (String, String, String, Priority, List<Int>) -> Unit = { _, _, _, _, _ -> },
    onDeleteClick: () -> Unit = {}
) {
    val orderId = booking.id.take(4).uppercase()
    
    val motorcycle = motorcycles.find { it.id == booking.motorcycleId }
    val vehicle = when {
        motorcycle != null -> "Unit: ${motorcycle.brand} ${motorcycle.model}".uppercase()
        booking.customBrand != null && booking.customModel != null -> "Unit: ${booking.customBrand} ${booking.customModel}".uppercase()
        booking.motorcycleId == "custom_unit" -> "Unit: CUSTOM UNIT"
        else -> "Unit: ${booking.motorcycleId.take(8).uppercase()}"
    }

    val customer = "User: ${booking.userId.take(8).uppercase()}"
    val status = booking.status.name
    val statusColor = when(booking.status) {
        BookingStatus.REPAIR -> Warning
        BookingStatus.WAITING_PART -> Secondary
        BookingStatus.READY_TO_PICK_UP -> Info
        BookingStatus.CONFIRMED -> Primary
        BookingStatus.PENDING -> Neutral
        BookingStatus.COMPLETED -> Primary
        BookingStatus.CANCELLED -> Secondary
    }
    val technician = booking.technicianName
    val priority = booking.priority.name

    var showStatusDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    var showConfirmCompleteDialog by remember { mutableStateOf(false) }
    var showConfirmCancelDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var serviceNotes by remember { mutableStateOf(booking.serviceNotes) }
    var workDescription by remember { mutableStateOf(booking.workDescription) }
    var techName by remember { mutableStateOf(booking.technicianName) }
    var selectedPriority by remember { mutableStateOf(booking.priority) }
    var selectedParts by remember { mutableStateOf(booking.usedPartIds) }

    val isHistory = booking.status == BookingStatus.COMPLETED || booking.status == BookingStatus.CANCELLED

    if (showStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStatusDialog = false },
            title = { Text("UPDATE STATUS", fontWeight = FontWeight.Black) },
            text = {
                Column {
                    BookingStatus.values()
                        .forEach { bookingStatus ->
                            TextButton(
                                onClick = {
                                    when (bookingStatus) {
                                        BookingStatus.COMPLETED -> showConfirmCompleteDialog = true
                                        BookingStatus.CANCELLED -> showConfirmCancelDialog = true
                                        else -> onStatusChange(bookingStatus)
                                    }
                                    showStatusDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(bookingStatus.name.replace("_", " "), color = Primary, fontWeight = FontWeight.Black)
                            }
                        }
                }
            },
            confirmButton = {}
        )
    }

    if (showConfirmCompleteDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmCompleteDialog = false },
            title = { Text("CONFIRM COMPLETION", fontWeight = FontWeight.Black) },
            text = { Text("Are you sure you want to mark this service as COMPLETED? This will automatically decrement stock for ${selectedParts.size} parts. Once completed, the status cannot be changed again.") },
            confirmButton = {
                Button(
                    onClick = {
                        onStatusChange(BookingStatus.COMPLETED)
                        showConfirmCompleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text("YES, COMPLETE", fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmCompleteDialog = false }) {
                    Text("CANCEL", fontWeight = FontWeight.Black)
                }
            },
            shape = RoundedCornerShape(0.dp)
        )
    }

    if (showConfirmCancelDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmCancelDialog = false },
            title = { Text("CONFIRM CANCELLATION", fontWeight = FontWeight.Black) },
            text = { Text("Are you sure you want to CANCEL this work order? This action will move the order to History and notify the user.") },
            confirmButton = {
                Button(
                    onClick = {
                        onStatusChange(BookingStatus.CANCELLED)
                        showConfirmCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text("YES, CANCEL", fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmCancelDialog = false }) {
                    Text("NO, KEEP ACTIVE", fontWeight = FontWeight.Black)
                }
            },
            shape = RoundedCornerShape(0.dp)
        )
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("DELETE HISTORY", fontWeight = FontWeight.Black) },
            text = { Text("Are you sure you want to delete this completed work order from history? This action cannot be undone.", fontWeight = FontWeight.Black) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClick()
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text("DELETE", fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("CANCEL", fontWeight = FontWeight.Black)
                }
            },
            shape = RoundedCornerShape(0.dp)
        )
    }

    if (showDetailsDialog) {
        AlertDialog(
            onDismissRequest = { showDetailsDialog = false },
            title = { Text("ASSIGNMENT & DETAILS", fontWeight = FontWeight.Black) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = techName,
                        onValueChange = { techName = it },
                        label = { Text("Assigned Technician") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(0.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Priority Level", fontSize = 12.sp, fontWeight = FontWeight.Black)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Priority.values().forEach { p ->
                            val isSelected = selectedPriority == p
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(32.dp)
                                    .background(if (isSelected) Primary else Color.White)
                                    .border(2.dp, Color.Black)
                                    .clickable { selectedPriority = p },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    p.name,
                                    fontSize = 8.sp,
                                    color = if (isSelected) Color.White else Neutral,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            if (p != Priority.URGENT) Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Inventory Allocation", fontSize = 12.sp, fontWeight = FontWeight.Black)
                    availableParts.filter { it.stockQuantity > 0 || selectedParts.contains(it.id) }.forEach { part ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedParts = if (selectedParts.contains(part.id)) {
                                        selectedParts - part.id
                                    } else {
                                        selectedParts + part.id
                                    }
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = selectedParts.contains(part.id),
                                onCheckedChange = { checked ->
                                    selectedParts = if (checked) {
                                        selectedParts + part.id
                                    } else {
                                        selectedParts - part.id
                                    }
                                }
                            )
                            Text("${part.name} (${part.stockQuantity} in stock)", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = workDescription,
                        onValueChange = { workDescription = it },
                        label = { Text("Work Description") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(0.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = serviceNotes,
                        onValueChange = { serviceNotes = it },
                        label = { Text("Service Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(0.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDetailsChange(serviceNotes, workDescription, techName, selectedPriority, selectedParts)
                        showDetailsDialog = false
                    },
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text("SAVE ASSIGNMENT", fontWeight = FontWeight.Black)
                }
            },
            shape = RoundedCornerShape(0.dp)
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(2.dp, Color.Black),
        color = Color.White,
        shape = RoundedCornerShape(0.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("ORDER #$orderId", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Neutral)
                    Text(vehicle, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    Text("Customer: $customer", fontSize = 12.sp, color = Neutral, fontWeight = FontWeight.Black)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "SERVICE: ${booking.workDescription.ifEmpty { "GENERAL MAINTENANCE" }.uppercase()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Primary
                    )
                    if (!booking.descriptionDetail.isNullOrBlank()) {
                        Text(
                            booking.descriptionDetail,
                            fontSize = 10.sp,
                            color = Neutral,
                            lineHeight = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "ESTIMATED COST: $${String.format(Locale.US, "%.2f", booking.totalPrice)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Secondary
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        color = statusColor,
                        shape = RoundedCornerShape(0.dp),
                        border = BorderStroke(1.dp, Color.Black)
                    ) {
                        Text(
                            status,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        priority,
                        color = if (priority == "URGENT" || priority == "HIGH") Secondary else Neutral,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            
            HorizontalDivider(color = Color.Black, thickness = 2.dp)
            
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(14.dp), tint = Neutral)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "TECH: ${technician.uppercase()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
                
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (isHistory) {
                        OutlinedButton(
                            onClick = { showDeleteConfirmDialog = true },
                            shape = RoundedCornerShape(0.dp),
                            border = BorderStroke(2.dp, Secondary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.height(32.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Secondary)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("DELETE", fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { showStatusDialog = true },
                            shape = RoundedCornerShape(0.dp),
                            border = BorderStroke(2.dp, Primary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("STATUS", color = Primary, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = { showDetailsDialog = true },
                        shape = RoundedCornerShape(0.dp),
                        border = BorderStroke(2.dp, Primary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp),
                        enabled = !isHistory
                    ) {
                        Text("DETAILS", color = Primary, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
