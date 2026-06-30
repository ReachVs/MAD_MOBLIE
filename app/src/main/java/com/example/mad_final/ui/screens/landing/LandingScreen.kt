package com.example.mad_final.ui.screens.landing

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.mad_final.ui.theme.*
import com.example.mad_final.ui.components.*

@Composable
fun LandingScreen(
    onBookServiceClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = RoundedCornerShape(0.dp),
                drawerContainerColor = Color.White
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("MAD APE", modifier = Modifier.padding(24.dp), fontWeight = FontWeight.Black, fontSize = 24.sp, letterSpacing = 2.sp)
                NavigationDrawerItem(
                    label = { Text("HOME", fontWeight = FontWeight.Black, fontSize = 12.sp) },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    shape = RoundedCornerShape(0.dp),
                    colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = LightGrid, selectedTextColor = Primary)
                )
                NavigationDrawerItem(
                    label = { Text("CATALOG SERVICE", fontWeight = FontWeight.Black, fontSize = 12.sp) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onLoginClick()
                    },
                    shape = RoundedCornerShape(0.dp)
                )
                NavigationDrawerItem(
                    label = { Text("SERVICE QUEUE", fontWeight = FontWeight.Black, fontSize = 12.sp) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onLoginClick()
                    },
                    shape = RoundedCornerShape(0.dp)
                )
                NavigationDrawerItem(
                    label = { Text("LOGIN / REGISTER", fontWeight = FontWeight.Black, fontSize = 12.sp) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onLoginClick()
                    },
                    shape = RoundedCornerShape(0.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = { 
                LandingTopBar(
                    onProfileClick = onLoginClick,
                    onMenuClick = { scope.launch { drawerState.open() } }
                ) 
            },
            bottomBar = { 
                LandingBottomNavigation(
                    onHomeClick = {},
                    onWorkshopClick = onLoginClick,
                    onBookClick = onLoginClick
                ) 
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                TechnicalGridBackground()
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { HeroSection(onActionClick = onBookServiceClick) }
                    item { BrandStatsSection() }
                    item { FeaturedServicesSection(onCatalogClick = onLoginClick) }
                    item { ContactSection() }
                    item { BrandFooterSection(showLinks = true) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingTopBar(onProfileClick: () -> Unit, onMenuClick: () -> Unit = {}) {
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
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(1.dp, Color.Black),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Outlined.Person, contentDescription = "Profile", modifier = Modifier.padding(4.dp))
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
        modifier = Modifier.drawBehind {
            drawLine(
                color = Color.Black,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 2.dp.toPx()
            )
        }
    )
}

@Composable
fun LandingBottomNavigation(
    onHomeClick: () -> Unit,
    onWorkshopClick: () -> Unit,
    onBookClick: () -> Unit
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
            selected = true,
            onClick = onHomeClick,
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
            label = { Text("CATALOG SERVICE", fontSize = 10.sp, fontWeight = FontWeight.Black) },
            selected = false,
            onClick = onWorkshopClick,
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Neutral, unselectedTextColor = Neutral, indicatorColor = Color.Transparent)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AddCircle, contentDescription = null) },
            label = { Text("BOOK SERVICE", fontSize = 10.sp, fontWeight = FontWeight.Black) },
            selected = false,
            onClick = onBookClick,
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Neutral, unselectedTextColor = Neutral, indicatorColor = Color.Transparent)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LandingScreenPreview() {
    MaterialTheme {
        LandingScreen()
    }
}
