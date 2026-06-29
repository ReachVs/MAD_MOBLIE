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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mad_final.ui.components.TechnicalGridBackground
import com.example.mad_final.ui.navigation.Screen
import com.example.mad_final.ui.theme.Primary
import com.example.mad_final.ui.theme.Secondary
import com.example.mad_final.ui.theme.Neutral
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun AdminRevenueScreen(
    onLogout: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onInventoryClick: () -> Unit = {},
    onQueueClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    onServicesClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val dailyRevenue by viewModel.dailyRevenue.collectAsStateWithLifecycle()
    val lifetimeRevenue by viewModel.lifetimeRevenue.collectAsStateWithLifecycle()
    val revenueData by viewModel.revenueData.collectAsStateWithLifecycle()
    val revenueByCategory by viewModel.revenueByCategory.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val adminImageUri by viewModel.adminImageUri.collectAsStateWithLifecycle()
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AdminDrawerContent(
                onDashboardClick = onHomeClick,
                onInventoryClick = onInventoryClick,
                onQueueClick = onQueueClick,
                onRevenueClick = { scope.launch { drawerState.close() } },
                onCalendarClick = {
                    scope.launch { drawerState.close() }
                    onCalendarClick()
                },
                onServicesClick = {
                    scope.launch { drawerState.close() }
                    onServicesClick()
                },
                onLogout = onLogout,
                onClose = { scope.launch { drawerState.close() } },
                currentRoute = Screen.AdminRevenue.route,
                userName = userName,
                userImageUri = adminImageUri
            )
        }
    ) {
        Scaffold(
            topBar = { 
                AdminTopBar(
                    title = "REVENUE ANALYTICS",
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onProfileClick = onProfileClick,
                    onCalendarClick = onCalendarClick,
                    userImageUri = adminImageUri
                ) 
            },
            bottomBar = { 
                AdminBottomNavigation(
                    onDashboardClick = onHomeClick,
                    onInventoryClick = onInventoryClick,
                    onQueueClick = onQueueClick,
                    currentRoute = Screen.AdminRevenue.route
                ) 
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
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                    
                    item {
                        RevenueCard(
                            title = "LIFETIME REVENUE",
                            totalRevenue = lifetimeRevenue,
                            dataPoints = emptyList<Double>(),
                            isLifetime = true
                        )
                    }
                    
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    
                    item {
                        RevenueCard(
                            title = "DAILY REVENUE",
                            totalRevenue = dailyRevenue,
                            dataPoints = revenueData
                        )
                    }
                    
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                    
                    item {
                        Text(
                            "REVENUE BY SERVICE CATEGORY",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Neutral,
                            letterSpacing = 1.sp
                        )
                    }
                    
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    
                    if (revenueByCategory.isEmpty()) {
                        item {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                border = BorderStroke(2.dp, Color.Black),
                                color = Color.White
                            ) {
                                Text(
                                    "NO CATEGORY DATA AVAILABLE",
                                    modifier = Modifier.padding(16.dp),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = Neutral,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    } else {
                        val maxRevenue = revenueByCategory.values.maxOrNull() ?: 1.0
                        items(revenueByCategory.toList().sortedByDescending { it.second }.size) { index ->
                            val (category, amount) = revenueByCategory.toList().sortedByDescending { it.second }[index]
                            CategoryRevenueItem(
                                category = category,
                                percentage = (amount / maxRevenue).toFloat(),
                                value = "$${String.format(Locale.US, "%,.2f", amount)}"
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }
}

@Composable
fun CategoryRevenueItem(category: String, percentage: Float, value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(2.dp, Color.Black),
        color = Color.White,
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(category, fontWeight = FontWeight.Black, fontSize = 14.sp)
                Text(value, fontWeight = FontWeight.Black, fontSize = 14.sp, color = Secondary)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { percentage },
                modifier = Modifier.fillMaxWidth().height(12.dp),
                color = Primary,
                trackColor = Color.LightGray.copy(alpha = 0.3f),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Butt
            )
        }
    }
}
