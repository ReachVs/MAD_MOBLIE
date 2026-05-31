package com.example.mad_final.ui.screens.admin

import com.example.mad_final.ui.navigation.Screen
import com.example.mad_final.ui.theme.Primary
import com.example.mad_final.ui.theme.Secondary
import com.example.mad_final.ui.theme.Neutral
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import com.example.mad_final.ui.components.TechnicalGridBackground
import java.util.Locale

// Colors from Style Guide
private val CriticalRed = Color(0xFF991B1B)
private val WarningOrange = Color(0xFFB45309)
private val OptimalGreen = Color(0xFF15803D)

@Preview(showBackground = true)
@Composable
fun AdminInventoryScreenPreview() {
    MaterialTheme {
        AdminInventoryScreen()
    }
}

@Composable
fun AdminInventoryScreen(
    onLogout: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onQueueClick: () -> Unit = {},
    onRevenueClick: () -> Unit = {},
    viewModel: AdminInventoryViewModel = hiltViewModel()
) {
    val parts by viewModel.parts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddPartDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, sku, qty, price, category ->
                viewModel.addPart(name, sku, qty, price, category)
                showAddDialog = false
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AdminDrawerContent(
                onDashboardClick = onHomeClick,
                onInventoryClick = { scope.launch { drawerState.close() } },
                onQueueClick = onQueueClick,
                onRevenueClick = {
                    scope.launch { drawerState.close() }
                    onRevenueClick()
                },
                onLogout = onLogout,
                onClose = {
                    scope.launch { drawerState.close() }
                },
                currentRoute = Screen.AdminInventory.route
            )
        }
    ) {
        Scaffold(
            topBar = { 
                AdminTopBar(
                    title = "INVENTORY",
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                ) 
            },
            bottomBar = { 
                AdminBottomNavigation(
                    onHomeClick = onHomeClick,
                    onInventoryClick = {},
                    onQueueClick = onQueueClick,
                    currentRoute = Screen.AdminInventory.route
                ) 
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
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
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                    
                    item { 
                        InventoryStatsHeader(
                            totalCount = parts.size,
                            criticalCount = parts.count { it.stockQuantity < 5 }, 
                            lowStockCount = parts.count { it.stockQuantity in 5..15 }
                        ) 
                    }
                    
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                    
                    item { 
                        InventorySearchBar(
                            query = searchQuery,
                            onQueryChange = { viewModel.onSearchQueryChange(it) }
                        ) 
                    }
                    
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    
                    item { 
                        InventoryFilters(
                            selectedCategory = selectedCategory,
                            onCategorySelect = { viewModel.onCategorySelect(it) }
                        ) 
                    }
                    
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                    
                    items(parts) { part ->
                        InventoryItemCard(
                            status = when {
                                part.stockQuantity == 0 -> "OUT OF STOCK"
                                part.stockQuantity < 10 -> "CRITICAL STOCK"
                                else -> "OPTIMAL"
                            },
                            statusColor = when {
                                part.stockQuantity == 0 -> CriticalRed
                                part.stockQuantity < 10 -> WarningOrange
                                else -> OptimalGreen
                            },
                            title = part.name,
                            sku = part.sku,
                            unitsLeft = part.stockQuantity,
                            minRequired = 10,
                            actionText = if (part.stockQuantity < 10) "REORDER NOW" else "VIEW DETAILS",
                            isOptimal = part.stockQuantity >= 10
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }
}

@Composable
fun InventoryStatsHeader(totalCount: Int, criticalCount: Int, lowStockCount: Int) {
    Column {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Primary,
            shape = RoundedCornerShape(0.dp),
            border = BorderStroke(2.dp, Color.Black)
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "TOTAL SKUS TRACKED",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "$totalCount",
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = Secondary,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp),
                color = Color.White,
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(2.dp, Color.Black)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "CRITICAL\nALERTS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Neutral
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "$criticalCount",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Secondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(120.dp),
                color = Color.White,
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(2.dp, Color.Black)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "LOW STOCK",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Neutral
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "$lowStockCount",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
fun InventorySearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search Inventory by SKU or Name", fontSize = 14.sp, fontWeight = FontWeight.Black) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
        shape = RoundedCornerShape(0.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedBorderColor = Color.Black,
            focusedBorderColor = Color.Black
        )
    )
}

@Composable
fun InventoryFilters(selectedCategory: String, onCategorySelect: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        val categories = listOf("ALL", "ENGINE", "TIRES", "FLUIDS")
        categories.forEachIndexed { index, category ->
            val isSelected = selectedCategory == category
            Surface(
                color = if (isSelected) Primary else Color.White,
                modifier = Modifier.weight(1f).clickable { onCategorySelect(category) },
                border = BorderStroke(2.dp, Color.Black),
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    if (category == "ENGINE") "ENGINE COMPONENTS" else category,
                    color = if (isSelected) Color.White else Neutral,
                    modifier = Modifier.padding(vertical = 12.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1
                )
            }
            if (index < categories.size - 1) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
fun InventoryItemCard(
    status: String,
    statusColor: Color,
    title: String,
    sku: String,
    unitsLeft: Int,
    minRequired: Int,
    actionText: String,
    isOptimal: Boolean = false
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(2.dp, Color.Black),
        color = Color.White,
        shape = RoundedCornerShape(0.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (isOptimal) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            status,
                            color = statusColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 24.sp
                    )
                    Text(
                        "SKU: $sku",
                        fontSize = 10.sp,
                        color = Neutral,
                        fontWeight = FontWeight.Black
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        String.format(Locale.getDefault(), "%02d", unitsLeft),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = if (status == "CRITICAL STOCK") CriticalRed else Color.Black
                    )
                    Text(
                        "UNITS\nLEFT",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.End,
                        lineHeight = 10.sp
                    )
                }
            }
            
            HorizontalDivider(color = Color.Black, thickness = 2.dp)
            
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Min. Required:", fontSize = 10.sp, color = Neutral, fontWeight = FontWeight.Black)
                    Text("$minRequired", fontSize = 14.sp, fontWeight = FontWeight.Black)
                }
                
                if (isOptimal) {
                    OutlinedButton(
                        onClick = { },
                        shape = RoundedCornerShape(0.dp),
                        border = BorderStroke(2.dp, Color.Black)
                    ) {
                        Text(actionText, color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                } else {
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                        shape = RoundedCornerShape(0.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text(actionText, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPartDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, Int, Double, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("ENGINE") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ADD NEW SKU", fontWeight = FontWeight.Black) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Part Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp)
                )
                OutlinedTextField(
                    value = sku,
                    onValueChange = { sku = it },
                    label = { Text("SKU") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp)
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Initial Stock") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp)
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Unit Price") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp)
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAdd(
                        name,
                        sku,
                        quantity.toIntOrNull() ?: 0,
                        price.toDoubleOrNull() ?: 0.0,
                        category
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(0.dp)
            ) {
                Text("ADD TO INVENTORY", fontWeight = FontWeight.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = Neutral, fontWeight = FontWeight.Black)
            }
        },
        shape = RoundedCornerShape(0.dp),
        containerColor = Color.White
    )
}
