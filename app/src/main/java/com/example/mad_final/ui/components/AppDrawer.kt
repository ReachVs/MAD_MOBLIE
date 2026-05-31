package com.example.mad_final.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mad_final.ui.navigation.Screen
import com.example.mad_final.ui.theme.Primary
import com.example.mad_final.ui.theme.Secondary

@Composable
fun TechnicalGridBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val gridSize = 20.dp.toPx()
        val gridColor = Color.LightGray.copy(alpha = 0.1f)
        
        for (x in 0..size.width.toInt() step gridSize.toInt()) {
            drawLine(
                color = gridColor,
                start = Offset(x.toFloat(), 0f),
                end = Offset(x.toFloat(), size.height),
                strokeWidth = 1f
            )
        }
        for (y in 0..size.height.toInt() step gridSize.toInt()) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y.toFloat()),
                end = Offset(size.width, y.toFloat()),
                strokeWidth = 1f
            )
        }
    }
}

@Composable
fun AppDrawerContent(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    onClose: () -> Unit,
    isAdmin: Boolean = false
) {
    ModalDrawerSheet(
        drawerContainerColor = Primary,
        drawerShape = RoundedCornerShape(0.dp),
        modifier = Modifier.width(320.dp)
    ) {
        Box {
            TechnicalGridBackground()
            
            Column {
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "MAD APE",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1).sp
                        )
                        Text(
                            if (isAdmin) "ADMIN TERMINAL" else "TECHNICAL SYSTEMS",
                            color = Secondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    }
                    
                    val placeholderPainter = rememberVectorPainter(Icons.Default.Refresh)
                    val errorPainter = rememberVectorPainter(Icons.Default.Warning)
                    
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?q=80&w=100&auto=format&fit=crop",
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(0.dp))
                            .background(Color.White.copy(alpha = 0.1f)),
                        contentScale = ContentScale.Crop,
                        placeholder = placeholderPainter,
                        error = errorPainter,
                        onLoading = { android.util.Log.d("AppDrawer", "Loading profile image") },
                        onError = { android.util.Log.e("AppDrawer", "Error loading profile image", it.result.throwable) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))
                
                if (isAdmin) {
                    DrawerItem(
                        label = "DASHBOARD",
                        icon = Icons.Default.Home,
                        selected = currentRoute == Screen.AdminDashboard.route,
                        onClick = { onNavigate(Screen.AdminDashboard.route); onClose() }
                    )
                    DrawerItem(
                        label = "INVENTORY",
                        icon = Icons.Default.Refresh,
                        selected = currentRoute == Screen.AdminInventory.route,
                        onClick = { onNavigate(Screen.AdminInventory.route); onClose() }
                    )
                    DrawerItem(
                        label = "QUEUE",
                        icon = Icons.Default.Build,
                        selected = currentRoute == Screen.AdminQueue.route,
                        onClick = { onNavigate(Screen.AdminQueue.route); onClose() }
                    )
                } else {
                    DrawerItem(
                        label = "DASHBOARD",
                        icon = Icons.Default.Home,
                        selected = currentRoute == Screen.Dashboard.route,
                        onClick = { onNavigate(Screen.Dashboard.route); onClose() }
                    )
                    DrawerItem(
                        label = "EXPLORE CATALOG",
                        icon = Icons.Default.Search,
                        selected = currentRoute == Screen.Catalog.route,
                        onClick = { onNavigate(Screen.Catalog.route); onClose() }
                    )
                    DrawerItem(
                        label = "MY BOOKINGS",
                        icon = Icons.Default.DateRange,
                        selected = currentRoute == Screen.MyBookings.route,
                        onClick = { onNavigate(Screen.MyBookings.route); onClose() }
                    )
                    DrawerItem(
                        label = "LIVE WORKSHOP",
                        icon = Icons.Default.Build,
                        selected = currentRoute == Screen.LiveFeed.route,
                        onClick = { onNavigate(Screen.LiveFeed.route); onClose() }
                    )
                    DrawerItem(
                        label = "BOOK SERVICE",
                        icon = Icons.Default.AddCircle,
                        selected = currentRoute?.startsWith("booking") == true,
                        onClick = { onNavigate(Screen.Booking.createRoute("custom_unit")); onClose() }
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))

                DrawerItem(
                    label = "LOGOUT",
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    selected = false,
                    onClick = { onLogout(); onClose() },
                    color = Secondary
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun DrawerItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color = Color.White
) {
    NavigationDrawerItem(
        label = { 
            Text(
                label, 
                fontWeight = FontWeight.Black, 
                letterSpacing = 1.sp,
                color = if (selected) Color.White else color.copy(alpha = 0.7f)
            ) 
        },
        selected = selected,
        onClick = onClick,
        icon = { 
            Icon(
                icon, 
                contentDescription = null, 
                tint = if (selected) Secondary else color.copy(alpha = 0.7f)
            ) 
        },
        shape = RoundedCornerShape(0.dp),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            selectedContainerColor = Color.White.copy(alpha = 0.1f)
        ),
        modifier = Modifier.padding(horizontal = 12.dp)
    )
}
