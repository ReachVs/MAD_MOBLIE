package com.example.mad_final.ui.screens.admin

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mad_final.ui.components.TechnicalGridBackground
import com.example.mad_final.ui.navigation.Screen
import com.example.mad_final.domain.models.Booking
import com.example.mad_final.domain.models.BookingStatus
import com.example.mad_final.domain.models.Priority

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
fun AdminDashboardScreenPreview() {
    MaterialTheme {
        AdminDashboardScreen()
    }
}

@Composable
fun AdminDashboardScreen(
    onLogout: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onInventoryClick: () -> Unit = {},
    onQueueClick: () -> Unit = {},
    onRevenueClick: () -> Unit = {},
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val bookings by viewModel.bookings.collectAsState()
    val dailyRevenue by viewModel.dailyRevenue.collectAsState()
    val dailyRevenueGrowth by viewModel.dailyRevenueGrowth.collectAsState()
    val lifetimeRevenue by viewModel.lifetimeRevenue.collectAsState()
    val totalCustomers by viewModel.totalCustomers.collectAsState()
    val activeOrdersCount by viewModel.activeOrdersCount.collectAsState()
    val urgentTasksCount by viewModel.urgentTasksCount.collectAsState()
    val revenueData by viewModel.revenueData.collectAsState()
    val services by viewModel.services.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    var showAddWorkOrderDialog by remember { mutableStateOf(false) }
    var showEditPriceDialog by remember { mutableStateOf<com.example.mad_final.domain.models.WorkshopService?>(null) }
    
    var motorcycleId by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var workDescription by remember { mutableStateOf("") }

    if (showEditPriceDialog != null) {
        var newPrice by remember { mutableStateOf(showEditPriceDialog?.price ?: "") }
        AlertDialog(
            onDismissRequest = { showEditPriceDialog = null },
            title = { Text("EDIT SERVICE PRICE", fontWeight = FontWeight.Black) },
            text = {
                Column {
                    Text(showEditPriceDialog?.title ?: "", fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPrice,
                        onValueChange = { newPrice = it },
                        label = { Text("New Price", fontWeight = FontWeight.Black) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(0.dp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateServicePrice(showEditPriceDialog!!.id, newPrice)
                        showEditPriceDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text("SAVE", fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditPriceDialog = null }) {
                    Text("CANCEL", fontWeight = FontWeight.Black, color = Neutral)
                }
            },
            shape = RoundedCornerShape(0.dp)
        )
    }

    if (showAddWorkOrderDialog) {
        AlertDialog(
            onDismissRequest = { showAddWorkOrderDialog = false },
            title = { Text("NEW WORK ORDER", fontWeight = FontWeight.Black) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = motorcycleId,
                        onValueChange = { motorcycleId = it },
                        label = { Text("Unit ID / VIN", fontWeight = FontWeight.Black) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(0.dp)
                    )
                    OutlinedTextField(
                        value = userId,
                        onValueChange = { userId = it },
                        label = { Text("Customer ID", fontWeight = FontWeight.Black) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(0.dp)
                    )
                    OutlinedTextField(
                        value = workDescription,
                        onValueChange = { workDescription = it },
                        label = { Text("Work Description", fontWeight = FontWeight.Black) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(0.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createWorkOrder(motorcycleId, userId, workDescription)
                        showAddWorkOrderDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text("CREATE", fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddWorkOrderDialog = false }) {
                    Text("CANCEL", fontWeight = FontWeight.Black, color = Neutral)
                }
            },
            shape = RoundedCornerShape(0.dp)
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AdminDrawerContent(
                onDashboardClick = { scope.launch { drawerState.close() } },
                onInventoryClick = onInventoryClick,
                onQueueClick = onQueueClick,
                onRevenueClick = { 
                    scope.launch { drawerState.close() }
                    onRevenueClick()
                },
                onLogout = onLogout,
                onClose = {
                    scope.launch { drawerState.close() }
                },
                currentRoute = Screen.AdminDashboard.route
            )
        }
    ) {
        Scaffold(
            topBar = { 
                AdminTopBar(
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                ) 
            },
            bottomBar = { 
                AdminBottomNavigation(
                    onHomeClick = {},
                    onInventoryClick = onInventoryClick,
                    onQueueClick = onQueueClick,
                    currentRoute = Screen.AdminDashboard.route
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddWorkOrderDialog = true },
                    containerColor = Primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Work Order")
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
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
                                "DASHBOARD",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-1).sp
                            )
                            Text(
                                "UPDATED: 09:41 AM",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Neutral
                            )
                        }
                    }

                    item { 
                        Box(modifier = Modifier.clickable { onQueueClick() }) {
                            DashboardSummarySection(
                                activeCount = activeOrdersCount,
                                urgentCount = urgentTasksCount,
                                customerCount = totalCustomers
                            )
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                    
                    item { 
                        Box(modifier = Modifier.clickable { onRevenueClick() }) {
                            RevenueCard(
                                title = "DAILY REVENUE",
                                totalRevenue = dailyRevenue, 
                                dataPoints = revenueData,
                                growth = dailyRevenueGrowth
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    item {
                        Box(modifier = Modifier.clickable { onInventoryClick() }) {
                            RevenueCard(
                                title = "LIFETIME REVENUE",
                                totalRevenue = lifetimeRevenue,
                                dataPoints = emptyList<Double>(),
                                isLifetime = true
                            )
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(32.dp)) }

                    item {
                        Text(
                            "SERVICE PRICING MANAGEMENT",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = Neutral
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }

                    items(services) { service ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showEditPriceDialog = service },
                            border = BorderStroke(2.dp, Color.Black),
                            color = Color.White,
                            shape = RoundedCornerShape(0.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(service.title.uppercase(), fontWeight = FontWeight.Black, fontSize = 16.sp)
                                    Text(service.category.uppercase(), fontSize = 10.sp, color = Neutral, fontWeight = FontWeight.Black)
                                }
                                Text(service.price, fontWeight = FontWeight.Black, fontSize = 18.sp, color = Secondary)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                    
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "ACTIVE WORK ORDERS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = Neutral
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { onQueueClick() }
                            ) {
                                Text(
                                    "FILTER",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Secondary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = "Filter",
                                    tint = Secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    
                    items(bookings.sortedByDescending { it.startDate }.take(5)) { booking ->
                        Box(modifier = Modifier.clickable { onQueueClick() }) {
                            WorkOrderCard(
                                orderId = booking.id.take(4).uppercase(),
                                vehicle = "Unit: ${booking.motorcycleId.take(8).uppercase()}",
                                customer = "User: ${booking.userId.take(8).uppercase()}",
                                timeInfo = java.text.SimpleDateFormat("HH:mm", java.util.Locale.US).format(java.util.Date(booking.startDate)),
                                status = when(booking.status) {
                                    BookingStatus.REPAIR -> "MAINTENANCE"
                                    BookingStatus.READY_TO_PICK_UP -> "READY"
                                    BookingStatus.WAITING_PART -> "AWAITING PARTS"
                                    else -> booking.status.name
                                },
                                statusColor = when(booking.status) {
                                    BookingStatus.REPAIR -> Warning
                                    BookingStatus.WAITING_PART -> Secondary
                                    BookingStatus.READY_TO_PICK_UP -> Info
                                    BookingStatus.CONFIRMED -> Primary
                                    BookingStatus.PENDING -> LightGrid
                                    BookingStatus.COMPLETED -> Success
                                    else -> Neutral
                                },
                                isUrgent = booking.priority == Priority.URGENT || booking.priority == Priority.HIGH
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }
}

@Composable
fun DashboardSummarySection(
    activeCount: Int,
    urgentCount: Int,
    customerCount: Int
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(140.dp),
            color = Primary,
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("ACTIVE\nORDERS", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.weight(1f))
                Text("$activeCount", color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Black)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp),
                color = Color.White,
                border = BorderStroke(2.dp, Color.Black),
                shape = RoundedCornerShape(0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("URGENT", fontSize = 10.sp, fontWeight = FontWeight.Black)
                    Text("$urgentCount", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Secondary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp),
                color = Color.White,
                border = BorderStroke(2.dp, Color.Black),
                shape = RoundedCornerShape(0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("CLIENTS", fontSize = 10.sp, fontWeight = FontWeight.Black)
                    Text("$customerCount", fontSize = 20.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun RevenueCard(
    title: String,
    totalRevenue: Double,
    dataPoints: List<Double>,
    growth: Double = 0.0,
    isLifetime: Boolean = false
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(2.dp, Color.Black),
        color = Color.White,
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(title, fontSize = 10.sp, fontWeight = FontWeight.Black, color = Neutral)
                    Text("$${String.format("%.2f", totalRevenue)}", fontSize = 32.sp, fontWeight = FontWeight.Black)
                }
                if (!isLifetime) {
                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Text(
                            "+${String.format("%.1f", growth)}%",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color(0xFF166534),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
            
            if (!isLifetime && dataPoints.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                // Simple technical bar chart
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    val maxVal = dataPoints.maxOrNull() ?: 0.0
                    dataPoints.forEach { point ->
                        val heightFactor = if (maxVal > 0) (point / maxVal).toFloat() else 0f
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(heightFactor.coerceIn(0.1f, 1.0f))
                                .background(Primary)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WorkOrderCard(
    orderId: String,
    vehicle: String,
    customer: String,
    timeInfo: String,
    status: String,
    statusColor: Color,
    isUrgent: Boolean = false
) {
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
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("ORDER #$orderId", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Neutral)
                    if (isUrgent) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = Secondary, shape = RoundedCornerShape(0.dp)) {
                            Text("URGENT", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }
                }
                Text(vehicle, fontSize = 16.sp, fontWeight = FontWeight.Black)
                Text(customer, fontSize = 12.sp, color = Neutral, fontWeight = FontWeight.Black)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(timeInfo, fontSize = 10.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = statusColor,
                    shape = RoundedCornerShape(0.dp),
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    val textColor = if (statusColor == LightGrid) Neutral else Color.White
                    Text(
                        status,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = textColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
