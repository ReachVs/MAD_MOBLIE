package com.example.mad_final.ui.screens.catalog

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mad_final.ui.components.AppDrawerContent
import com.example.mad_final.ui.components.TechnicalGridBackground
import com.example.mad_final.ui.navigation.Screen
import com.example.mad_final.domain.models.WorkshopService
import com.example.mad_final.ui.theme.Primary
import com.example.mad_final.ui.theme.Secondary
import com.example.mad_final.ui.theme.Neutral
import com.example.mad_final.ui.components.legacy.LegacyServiceList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onLogout: () -> Unit = {},
    onServiceClick: (String) -> Unit,
    onHomeClick: () -> Unit,
    onBookClick: () -> Unit,
    onTrackingClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val groupedServices by viewModel.groupedServices.collectAsStateWithLifecycle()
    val hasActiveService by viewModel.hasActiveService.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedServiceIds by viewModel.selectedServiceIds.collectAsStateWithLifecycle()
    val selectedServices by viewModel.selectedServices.collectAsStateWithLifecycle()
    val totalPrice by viewModel.totalPrice.collectAsStateWithLifecycle()
    val totalDuration by viewModel.totalDuration.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userImageUri by viewModel.userImageUri.collectAsStateWithLifecycle()

    val expandedCategories = remember { mutableStateMapOf<String, Boolean>() }
    val expandedSubCategories = remember { mutableStateMapOf<String, Boolean>() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                currentRoute = Screen.Catalog.route,
                userName = userName,
                userImageUri = userImageUri,
                onNavigate = { route ->
                    when (route) {
                        Screen.Dashboard.route -> onHomeClick()
                        Screen.Catalog.route -> scope.launch { drawerState.close() }
                        Screen.MyBookings.route -> onTrackingClick()
                        Screen.Booking.createRoute("custom_unit") -> onBookClick()
                        Screen.Profile.route -> onProfileClick()
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
                    userImageUri = userImageUri,
                    onProfileClick = onProfileClick,
                    onMenuClick = { scope.launch { drawerState.open() } }
                ) 
            },
            bottomBar = { 
                com.example.mad_final.ui.components.MadApeBottomNavigation(
                    currentRoute = "catalog",
                    hasActiveService = hasActiveService,
                    onHomeClick = onHomeClick,
                    onCatalogClick = {},
                    onTrackingClick = {
                        onTrackingClick()
                    },
                    onBookClick = onBookClick
                ) 
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                TechnicalGridBackground()
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    item {
                        CatalogHeader(
                            selectedCategory = selectedCategory,
                            onCategorySelect = viewModel::setCategory
                        )
                    }

                    groupedServices.forEach { (category, groupedBySub) ->
                        item(key = category) {
                            val isExpanded = expandedCategories[category] ?: false
                            
                            MainCategoryCard(
                                category = category,
                                imageUrl = groupedBySub.values.firstOrNull()?.firstOrNull()?.imageUrl ?: "",
                                isExpanded = isExpanded,
                                onExpandToggle = { expandedCategories[category] = !isExpanded }
                            ) {
                                groupedBySub.forEach { (subCategory, subServices) ->
                                    val subKey = "$category-$subCategory"
                                    val isSubExpanded = expandedSubCategories[subKey] ?: false
                                    
                                    SubCategoryCard(
                                        subCategory = subCategory,
                                        isExpanded = isSubExpanded,
                                        onExpandToggle = { expandedSubCategories[subKey] = !isSubExpanded }
                                    ) {
                                        LegacyServiceList(
                                            services = subServices,
                                            selectedIds = selectedServiceIds,
                                            onToggle = { viewModel.toggleService(it) }
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }

                    item {
                        FooterSection()
                    }
                }

                // Sticky Checkout Bar
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                ) {
                    var showSummary by remember { mutableStateOf(false) }
                    
                    Column {
                        if (showSummary && selectedServices.isNotEmpty()) {
                            SelectedServicesSummary(
                                selectedServices = selectedServices,
                                onRemove = viewModel::toggleService,
                                onClose = { showSummary = false }
                            )
                        }

                        CheckoutFloatingBar(
                            selectedCount = selectedServiceIds.size,
                            totalPrice = totalPrice,
                            totalDuration = totalDuration,
                            onCheckoutClick = {
                                val serviceIdsParam = selectedServiceIds.joinToString(",")
                                onServiceClick(serviceIdsParam)
                            },
                            onToggleSummary = { showSummary = !showSummary },
                            isSummaryVisible = showSummary,
                            onClearAll = viewModel::clearSelection
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CatalogHeader(
    selectedCategory: String,
    onCategorySelect: (String) -> Unit
) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            "CATALOG SERVICE",
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
            onCategorySelect = onCategorySelect
        )
    }
}

@Composable
fun SelectedServicesSummary(
    selectedServices: List<WorkshopService>,
    onRemove: (String) -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 0.dp), // Connect to the bar below
        color = Color.White,
        border = BorderStroke(2.dp, Color.Black),
        shadowElevation = 16.dp,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "SHOPPING CART",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                    Text(
                        "${selectedServices.size} ITEMS READY FOR TUNING",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Secondary
                    )
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.Black, CircleShape)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Scrollable list if too many items
            Column(
                modifier = Modifier
                    .heightIn(max = 300.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                selectedServices.forEach { service ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(4.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color.Black, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                service.title.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                maxLines = 1
                            )
                            Text(
                                "RM ${String.format(java.util.Locale.US, "%.2f", service.price)}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Secondary
                            )
                        }
                        
                        IconButton(
                            onClick = { onRemove(service.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Remove",
                                tint = Color.Red.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainCategoryCard(
    category: String,
    imageUrl: String,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val imageModel = remember(imageUrl) {
        val url = imageUrl.trim()
        if (url.startsWith("http")) url else {
            val resId = context.resources.getIdentifier(url, "drawable", context.packageName)
            if (resId != 0) resId else null
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .border(2.dp, Color.Black)
            .animateContentSize(),
        color = Color.White,
        shape = RoundedCornerShape(0.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clickable { onExpandToggle() }
            ) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                                startY = 200f
                            )
                        )
                )
                Surface(
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        text = category.uppercase(),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
                Icon(
                    if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .size(24.dp)
                )
            }

            if (isExpanded) {
                Column(modifier = Modifier.padding(16.dp)) {
                    content()
                }
            }
        }
    }
}

@Composable
fun SubCategoryCard(
    subCategory: String,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpandToggle() }
                .background(if (isExpanded) Color.Black.copy(alpha = 0.03f) else Color.Transparent)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = subCategory.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = if (isExpanded) Primary else Secondary
            )
            Icon(
                if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Secondary
            )
        }
        
        if (isExpanded) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                content()
            }
        }
        HorizontalDivider(color = Color.Black.copy(alpha = 0.05f))
    }
}

@Composable
fun ServiceListItem(
    service: WorkshopService,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        color = if (isSelected) Color.Black else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = null, // Handled by parent surface click
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.White,
                    uncheckedColor = Color.Black,
                    checkmarkColor = Color.Black
                )
            )
            
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text(
                    text = service.title.uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isSelected) Color.White else Color.Black
                )
                Text(
                    text = "EST. ${service.duration.uppercase()}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White.copy(alpha = 0.6f) else Secondary
                )
            }
            
            Text(
                text = "RM ${String.format(java.util.Locale.US, "%.2f", service.price)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = if (isSelected) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun CheckoutFloatingBar(
    selectedCount: Int,
    totalPrice: Double,
    totalDuration: Int,
    onCheckoutClick: () -> Unit,
    onToggleSummary: () -> Unit = {},
    isSummaryVisible: Boolean = false,
    onClearAll: () -> Unit = {}
) {
    if (selectedCount == 0) return

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color.Black,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 12.dp,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onToggleSummary() }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$selectedCount SERVICES",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Icon(
                        if (isSummaryVisible) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        color = Color.Red,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.clickable { onClearAll() }
                    ) {
                        Text(
                            "CLEAR ALL",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "RM ${String.format(java.util.Locale.US, "%.2f", totalPrice)}",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "EST. DURATION: ${totalDuration} MINS",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Button(
                onClick = onCheckoutClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(4.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "BOOK NOW",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
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
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FilterButton("ALL", selectedCategory == "ALL") { onCategorySelect("ALL") }
        FilterButton("MAINTENANCE", selectedCategory == "MAINTENANCE SERVICES") { onCategorySelect("MAINTENANCE SERVICES") }
        FilterButton("WASHING", selectedCategory == "WASHING") { onCategorySelect("WASHING") }
        FilterButton("ENGINE", selectedCategory == "ENGINE CHECK UP") { onCategorySelect("ENGINE CHECK UP") }
        FilterButton("TUNING", selectedCategory == "TUNING PERFORMANCE") { onCategorySelect("TUNING PERFORMANCE") }
    }
}

@Composable
fun FilterButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) Color.Black else Color.White,
        border = BorderStroke(2.dp, Color.Black),
        modifier = Modifier
            .height(38.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(0.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text,
                color = if (isSelected) Color.White else Color.Black,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogTopBar(
    userImageUri: String?,
    onProfileClick: () -> Unit, 
    onMenuClick: () -> Unit = {}
) {
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
                val placeholderPainter = rememberVectorPainter(Icons.Default.Person)
                val errorPainter = rememberVectorPainter(Icons.Default.Warning)

                AsyncImage(
                    model = userImageUri,
                    contentDescription = "User Profile",
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
fun FooterSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
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
