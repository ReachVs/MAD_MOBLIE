package com.example.mad_final.ui.screens.landing

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview

import com.example.mad_final.R
import com.example.mad_final.ui.theme.Primary
import com.example.mad_final.ui.theme.Secondary
import com.example.mad_final.ui.theme.Neutral
import com.example.mad_final.ui.theme.Background
import com.example.mad_final.ui.theme.LightGrid
import com.example.mad_final.ui.components.TechnicalGridBackground

@Preview(showBackground = true)
@Composable
fun LandingScreenPreview() {
    MaterialTheme {
        LandingScreen()
    }
}

// Colors from Style Guide
val BrandPrimary = Color(0xFF1E293B)
val BrandSecondary = Color(0xFF991B1B)
val BrandNeutral = Color(0xFF787778)
val GridColor = Color(0xFFE2E8F0)

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
                    label = { Text("BOOK SERVICE", fontWeight = FontWeight.Black, fontSize = 12.sp) },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onBookServiceClick()
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
                    onBookClick = onLoginClick,
                    onProfileClick = onLoginClick
                ) 
            }
        ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            TechnicalGridBackground()
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // Hero Section
                item { HeroSection(onLoginClick) }

                // Grid Info Section
                item { GridInfoSection() }

                // Curation Section
                item { CurationSection() }

                // Footer Section
                item { FooterSection() }
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
            Text(
                "MAD APE",
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontSize = 20.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(onClick = onProfileClick) {
                Surface(
                    shape = RoundedCornerShape(0.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color.Black),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Outlined.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.padding(4.dp)
                    )
                }
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
                strokeWidth = 2.dp.toPx()
            )
        }
    )
}

@Composable
fun HeroSection(onBookServiceClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .drawBehind {
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2.dp.toPx()
                )
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.washing),
            contentDescription = "Hero Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "PRECISION\nENGINEERED",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                lineHeight = 46.sp,
                letterSpacing = (-1).sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Custom builds and technical service for the modern enthusiast.",
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                modifier = Modifier.width(280.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onBookServiceClick,
                colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(2.dp, Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    "BOOK SERVICE",
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun GridInfoSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Card 1
        Surface(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(2.dp, Color.Black),
            color = Color.White,
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Λ", color = Secondary, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(48.dp))
                Text("0.01mm", fontSize = 32.sp, fontWeight = FontWeight.Black)
                Text("TOLERANCE THRESHOLD", fontSize = 10.sp, color = Neutral, fontWeight = FontWeight.Black)
            }
        }

        // Card 2
        Surface(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(2.dp, Color.Black),
            color = Color.White,
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("CLINICAL", fontWeight = FontWeight.Black, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Our workshop operates with the sterile discipline of an operating theater. Every bolt is torqued to exact manufacturer specifications.",
                    fontSize = 14.sp,
                    color = Neutral,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.Black, thickness = 2.dp)
            }
        }

        // Card 3 (Dark)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Primary,
            border = BorderStroke(2.dp, Color.Black),
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Icon(Icons.Default.Build, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("OPS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Text("FULL RESTORATION & PERFORMANCE TUNING", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun CurationSection() {
    val placeholderPainter = rememberVectorPainter(Icons.Default.Refresh)
    val errorPainter = rememberVectorPainter(Icons.Default.Warning)

    Column(modifier = Modifier.padding(24.dp)) {
        Text("CURATION BY", fontSize = 10.sp, color = Secondary, fontWeight = FontWeight.Black)
        Text("THE TUNING\nLAB", fontSize = 32.sp, fontWeight = FontWeight.Black, lineHeight = 36.sp)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Surface(
            border = BorderStroke(2.dp, Color.Black),
            shape = RoundedCornerShape(0.dp)
        ) {
            Column {
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1611002526569-808447d4323e?q=80&w=2070&auto=format&fit=crop",
                    contentDescription = "Curation Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = placeholderPainter,
                    error = errorPainter,
                    onLoading = { android.util.Log.d("LandingScreen", "Loading curation image") },
                    onError = { android.util.Log.e("LandingScreen", "Error loading curation image", it.result.throwable) }
                )
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("DIAGNOSTICS", fontSize = 10.sp, color = Neutral, fontWeight = FontWeight.Black)
                    Text("Electronic Systems Calibration", fontSize = 16.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun FooterSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Primary)
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "MAD APE",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp,
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FooterLink("PRIVACY")
            FooterLink("TERMS")
            FooterLink("SUPPORT")
        }
        Spacer(modifier = Modifier.height(8.dp))
        FooterLink("CONTACT")

        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            "© 2024 MAD APE MOTORWORKS. PRECISION ENGINEERED.",
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 8.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun FooterLink(text: String) {
    Text(text, color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Black)
}

@Composable
fun LandingBottomNavigation(
    onHomeClick: () -> Unit,
    onWorkshopClick: () -> Unit,
    onBookClick: () -> Unit,
    onProfileClick: () -> Unit
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
            label = { Text("WORKSHOP", fontSize = 10.sp, fontWeight = FontWeight.Black) },
            selected = false,
            onClick = onWorkshopClick,
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Neutral,
                unselectedTextColor = Neutral,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AddCircle, contentDescription = null) },
            label = { Text("BOOK", fontSize = 10.sp, fontWeight = FontWeight.Black) },
            selected = false,
            onClick = onBookClick,
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Neutral,
                unselectedTextColor = Neutral,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("PROFILE", fontSize = 10.sp, fontWeight = FontWeight.Black) },
            selected = false,
            onClick = onProfileClick,
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Neutral,
                unselectedTextColor = Neutral,
                indicatorColor = Color.Transparent
            )
        )
    }
}
