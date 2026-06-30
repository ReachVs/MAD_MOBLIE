package com.example.mad_final.ui.screens.admin

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

import com.example.mad_final.ui.theme.Primary
import com.example.mad_final.ui.theme.Secondary
import com.example.mad_final.ui.theme.Neutral
import com.example.mad_final.ui.theme.Background

import com.example.mad_final.ui.navigation.Screen

@Composable
fun AdminDrawerContent(
    onDashboardClick: () -> Unit = {},
    onInventoryClick: () -> Unit = {},
    onQueueClick: () -> Unit = {},
    onRevenueClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    onServicesClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    onClose: () -> Unit = {},
    currentRoute: String = "",
    userName: String? = null,
    userImageUri: String? = null
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        drawerShape = RoundedCornerShape(0.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    userName?.uppercase() ?: "MAD APE ADMIN",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "SYSTEM ADMINISTRATOR",
                    color = Primary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            
            val placeholderPainter = rememberVectorPainter(Icons.Default.Person)
            val errorPainter = rememberVectorPainter(Icons.Default.Warning)
            
            AsyncImage(
                model = userImageUri,
                contentDescription = "Admin Profile",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Black, CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onProfileClick()
                        onClose()
                    },
                contentScale = ContentScale.Crop,
                placeholder = placeholderPainter,
                error = errorPainter
            )
        }
        HorizontalDivider(thickness = 2.dp, color = Color.Black)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        NavigationDrawerItem(
            label = { Text("DASHBOARD", fontWeight = FontWeight.Black) },
            selected = currentRoute == Screen.AdminDashboard.route,
            onClick = {
                onDashboardClick()
                onClose()
            },
            icon = { Icon(Icons.Default.GridView, contentDescription = null) },
            shape = RoundedCornerShape(0.dp),
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = Color.Transparent,
                selectedContainerColor = Primary.copy(alpha = 0.1f),
                selectedTextColor = Primary,
                selectedIconColor = Primary
            )
        )

        NavigationDrawerItem(
            label = { Text("INVENTORY", fontWeight = FontWeight.Black) },
            selected = currentRoute == Screen.AdminInventory.route,
            onClick = {
                onInventoryClick()
                onClose()
            },
            icon = { Icon(Icons.Default.Inventory, contentDescription = null) },
            shape = RoundedCornerShape(0.dp),
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = Color.Transparent,
                selectedContainerColor = Primary.copy(alpha = 0.1f),
                selectedTextColor = Primary,
                selectedIconColor = Primary
            )
        )

        NavigationDrawerItem(
            label = { Text("SERVICE QUEUE", fontWeight = FontWeight.Black) },
            selected = currentRoute == Screen.AdminQueue.route,
            onClick = {
                onQueueClick()
                onClose()
            },
            icon = { Icon(Icons.Default.Build, contentDescription = null) },
            shape = RoundedCornerShape(0.dp),
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = Color.Transparent,
                selectedContainerColor = Primary.copy(alpha = 0.1f),
                selectedTextColor = Primary,
                selectedIconColor = Primary
            )
        )

        NavigationDrawerItem(
            label = { Text("REVENUE", fontWeight = FontWeight.Black) },
            selected = currentRoute == Screen.AdminRevenue.route,
            onClick = {
                onRevenueClick()
                onClose()
            },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
            shape = RoundedCornerShape(0.dp),
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = Color.Transparent,
                selectedContainerColor = Primary.copy(alpha = 0.1f),
                selectedTextColor = Primary,
                selectedIconColor = Primary
            )
        )

        NavigationDrawerItem(
            label = { Text("CALENDAR", fontWeight = FontWeight.Black) },
            selected = currentRoute == Screen.AdminCalendar.route,
            onClick = {
                onCalendarClick()
                onClose()
            },
            icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            shape = RoundedCornerShape(0.dp),
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = Color.Transparent,
                selectedContainerColor = Primary.copy(alpha = 0.1f),
                selectedTextColor = Primary,
                selectedIconColor = Primary
            )
        )

        NavigationDrawerItem(
            label = { Text("SERVICES", fontWeight = FontWeight.Black) },
            selected = currentRoute == Screen.AdminServices.route,
            onClick = {
                onServicesClick()
                onClose()
            },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            shape = RoundedCornerShape(0.dp),
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = Color.Transparent,
                selectedContainerColor = Primary.copy(alpha = 0.1f),
                selectedTextColor = Primary,
                selectedIconColor = Primary
            )
        )
        
        Spacer(modifier = Modifier.weight(1f))

        NavigationDrawerItem(
            label = { Text("LOGOUT", fontWeight = FontWeight.Black, color = Secondary) },
            selected = false,
            onClick = {
                onLogout()
                onClose()
            },
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Secondary) },
            shape = RoundedCornerShape(0.dp),
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = Color.Transparent,
                selectedContainerColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun AdminBottomNavigation(
    onDashboardClick: () -> Unit,
    onInventoryClick: () -> Unit,
    onQueueClick: () -> Unit,
    currentRoute: String = ""
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
            icon = { Icon(Icons.Default.GridView, contentDescription = null) },
            label = { Text("DASHBOARD", fontSize = 10.sp, fontWeight = FontWeight.Black) },
            selected = currentRoute == Screen.AdminDashboard.route,
            onClick = onDashboardClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Secondary,
                selectedTextColor = Secondary,
                unselectedIconColor = Neutral,
                unselectedTextColor = Neutral,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Inventory, contentDescription = null) },
            label = { Text("INVENTORY", fontSize = 10.sp, fontWeight = FontWeight.Black) },
            selected = currentRoute == Screen.AdminInventory.route,
            onClick = onInventoryClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Secondary,
                selectedTextColor = Secondary,
                unselectedIconColor = Neutral,
                unselectedTextColor = Neutral,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Build, contentDescription = null) },
            label = { Text("SERVICE QUEUE", fontSize = 10.sp, fontWeight = FontWeight.Black) },
            selected = currentRoute == Screen.AdminQueue.route,
            onClick = onQueueClick,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTopBar(
    title: String = "MAD APE ADMIN",
    userImageUri: String? = null,
    onMenuClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCalendarClick: (() -> Unit)? = null,
    showSearch: Boolean = false
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                title,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontSize = title.let { if (it.length > 10) 16.sp else 20.sp }
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            if (onCalendarClick != null) {
                IconButton(onClick = onCalendarClick) {
                    Icon(Icons.Default.DateRange, contentDescription = "Calendar")
                }
            }
            if (showSearch) {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
            IconButton(onClick = onProfileClick) {
                val placeholderPainter = rememberVectorPainter(Icons.Default.Person)
                val errorPainter = rememberVectorPainter(Icons.Default.Warning)
                
                AsyncImage(
                    model = userImageUri,
                    contentDescription = "Admin Profile",
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
