package com.example.mad_final.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    isAdmin: Boolean = false,
    userName: String? = null,
    userImageUri: String? = null
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            userName?.uppercase() ?: "MAD APE",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1).sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            if (isAdmin) "ADMIN TERMINAL" else "TECHNICAL SYSTEMS",
                            color = Secondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    }
                    
                    val placeholderPainter = rememberVectorPainter(Icons.Default.Person)
                    val errorPainter = rememberVectorPainter(Icons.Default.Warning)
                    
                    AsyncImage(
                        model = userImageUri,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Black, CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onNavigate(Screen.Profile.route)
                                onClose()
                            },
                        contentScale = ContentScale.Crop,
                        placeholder = placeholderPainter,
                        error = errorPainter
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))
                
                if (isAdmin) {
                    DrawerItem(
                        label = "DASHBOARD",
                        icon = Icons.Default.GridView,
                        selected = currentRoute == Screen.AdminDashboard.route,
                        onClick = { onNavigate(Screen.AdminDashboard.route); onClose() }
                    )
                    DrawerItem(
                        label = "INVENTORY",
                        icon = Icons.Default.Inventory,
                        selected = currentRoute == Screen.AdminInventory.route,
                        onClick = { onNavigate(Screen.AdminInventory.route); onClose() }
                    )
                    DrawerItem(
                        label = "SERVICE QUEUE",
                        icon = Icons.Default.Build,
                        selected = currentRoute == Screen.AdminQueue.route,
                        onClick = { onNavigate(Screen.AdminQueue.route); onClose() }
                    )
                    DrawerItem(
                        label = "SERVICES",
                        icon = Icons.Default.Settings,
                        selected = currentRoute == Screen.AdminServices.route,
                        onClick = { onNavigate(Screen.AdminServices.route); onClose() }
                    )
                } else {
                    DrawerItem(
                        label = "HUB",
                        icon = Icons.Default.Settings,
                        selected = currentRoute == Screen.Dashboard.route,
                        onClick = { onNavigate(Screen.Dashboard.route); onClose() }
                    )
                    DrawerItem(
                        label = "CATALOG SERVICE",
                        icon = Icons.AutoMirrored.Filled.List,
                        selected = currentRoute == Screen.Catalog.route,
                        onClick = { onNavigate(Screen.Catalog.route); onClose() }
                    )
                    DrawerItem(
                        label = "SERVICE QUEUE",
                        icon = Icons.Default.Build,
                        selected = currentRoute == Screen.MyBookings.route,
                        onClick = { onNavigate(Screen.MyBookings.route); onClose() }
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
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "SYSTEM TERMINATION",
                    modifier = Modifier.padding(horizontal = 24.dp),
                    color = Color.White.copy(alpha = 0.3f),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
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
