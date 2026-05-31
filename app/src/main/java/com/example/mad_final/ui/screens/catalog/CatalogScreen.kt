package com.example.mad_final.ui.screens.catalog

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mad_final.ui.components.AppDrawerContent
import com.example.mad_final.ui.components.TechnicalGridBackground
import com.example.mad_final.ui.navigation.Screen
import com.example.mad_final.domain.models.WorkshopService

import com.example.mad_final.ui.theme.Primary
import com.example.mad_final.ui.theme.Secondary
import com.example.mad_final.ui.theme.Neutral
import com.example.mad_final.ui.theme.Background

private val GridColor = Color(0xFFE2E8F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onLogout: () -> Unit = {},
    onServiceClick: (String) -> Unit,
    onHomeClick: () -> Unit,
    onBookClick: () -> Unit,
    onLiveFeedClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val services by viewModel.services.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                currentRoute = Screen.Catalog.route,
                onNavigate = { route ->
                    when (route) {
                        Screen.Dashboard.route -> onHomeClick()
                        Screen.Catalog.route -> scope.launch { drawerState.close() }
                        Screen.LiveFeed.route -> onLiveFeedClick()
                        Screen.MyBookings.route -> onProfileClick()
                        Screen.Booking.createRoute("custom_unit") -> onBookClick()
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
                CatalogTopBar(
                    onProfileClick = onProfileClick,
                    onMenuClick = { scope.launch { drawerState.open() } }
                ) 
            },
            bottomBar = { 
                CatalogBottomNavigation(
                    onHomeClick = onHomeClick,
                    onCatalogClick = {},
                    onBookClick = onBookClick,
                    onProfileClick = onProfileClick
                ) 
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                TechnicalGridBackground()
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                item {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            "SERVICE CATALOG",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Secondary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "PRECISION\nMAINTENANCE FOR\nELITE MACHINES.",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            lineHeight = 34.sp,
                            letterSpacing = (-1).sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .width(48.dp)
                                .height(4.dp)
                                .background(Color.Black)
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        ServiceFilterSection(
                            selectedCategory = selectedCategory,
                            onCategorySelect = { viewModel.setCategory(it) }
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }

                items(services) { service ->
                    ServiceTechnicalCard(
                        service = service,
                        onClick = { onServiceClick(service.id) }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    FooterSection()
                }
            }
        }
    }
}
}

@Composable
fun ServiceFilterSection(
    selectedCategory: String,
    onCategorySelect: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FilterButton(
            text = "ALL SERVICES",
            isSelected = selectedCategory == "ALL",
            onClick = { onCategorySelect("ALL") }
        )
        FilterButton(
            text = "PERFORMANCE",
            isSelected = selectedCategory == "PERFORMANCE",
            onClick = { onCategorySelect("PERFORMANCE") }
        )
        FilterButton(
            text = "MAINTENANCE",
            isSelected = selectedCategory == "MAINTENANCE",
            onClick = { onCategorySelect("MAINTENANCE") }
        )
    }
}

@Composable
fun FilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) Primary else Color.White,
        border = if (isSelected) null else BorderStroke(2.dp, Color.Black),
        modifier = Modifier
            .height(38.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(0.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text,
                color = if (isSelected) Color.White else Neutral,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun ServiceTechnicalCard(
    service: WorkshopService,
    onClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val imageModel = remember(service.imageUrl) {
        val url = service.imageUrl.trim()
        if (url.startsWith("http")) {
            url
        } else if (url.isNotEmpty()) {
            val resId = context.resources.getIdentifier(url, "drawable", context.packageName)
            if (resId != 0) resId else null
        } else {
            null
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable(onClick = onClick),
        border = BorderStroke(2.dp, Color.Black),
        color = Color.White,
        shape = RoundedCornerShape(0.dp)
    ) {
        Column {
            Box {
                val placeholderPainter = rememberVectorPainter(Icons.Default.Refresh)
                val errorPainter = rememberVectorPainter(Icons.Default.Warning)
                
                AsyncImage(
                    model = imageModel,
                    contentDescription = "Image for ${service.title}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = placeholderPainter,
                    error = errorPainter,
                    onLoading = { android.util.Log.d("CatalogScreen", "Loading image: $imageModel") },
                    onError = { android.util.Log.e("CatalogScreen", "Error loading image: $imageModel", it.result.throwable) }
                )
                
                Surface(
                    color = Secondary,
                    modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        service.category.uppercase(),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = service.title.uppercase(),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 28.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = service.price,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = service.description,
                    fontSize = 14.sp,
                    color = Neutral,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    service.tags.take(2).forEach { tag ->
                        Surface(
                            border = BorderStroke(1.dp, Color.Black),
                            modifier = Modifier.height(48.dp).weight(0.5f),
                            shape = RoundedCornerShape(0.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(tag.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Black, color = Neutral)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    
                    Button(
                        onClick = onClick,
                        modifier = Modifier.height(48.dp).weight(1f),
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        border = BorderStroke(2.dp, Color.Black)
                    ) {
                        Text("SPECIFICATIONS", fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogTopBar(onProfileClick: () -> Unit, onMenuClick: () -> Unit = {}) {
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
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?q=80&w=100&auto=format&fit=crop",
                    contentDescription = "User Profile",
                    modifier = Modifier
                        .size(32.dp)
                        .border(1.dp, Color.Black),
                    contentScale = ContentScale.Crop
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
                cap = androidx.compose.ui.graphics.StrokeCap.Square
            )
        }
    )
}

@Composable
fun CatalogBottomNavigation(
    onHomeClick: () -> Unit,
    onCatalogClick: () -> Unit,
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
            selected = false,
            onClick = onHomeClick,
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Neutral,
                unselectedTextColor = Neutral,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
            label = { Text("CATALOG", fontSize = 10.sp, fontWeight = FontWeight.Black) },
            selected = true,
            onClick = onCatalogClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Secondary,
                selectedTextColor = Secondary,
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
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            "© 2024 MAD APE MOTORWORKS. PRECISION ENGINEERED.",
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 8.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}
