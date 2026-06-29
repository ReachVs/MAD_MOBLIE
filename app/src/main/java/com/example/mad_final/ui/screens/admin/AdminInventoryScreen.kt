package com.example.mad_final.ui.screens.admin

import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.mad_final.domain.models.Part
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
import java.text.SimpleDateFormat
import java.util.Date

// Colors from Style Guide
private val CriticalRed = Color(0xFF991B1B)
private val WarningOrange = Color(0xFFB45309)
private val OptimalGrey = Color(0xFF1E293B)

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
    onCalendarClick: () -> Unit = {},
    onServicesClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: AdminInventoryViewModel = hiltViewModel()
) {
    val parts by viewModel.parts.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val totalCount by viewModel.totalSkus.collectAsStateWithLifecycle()
    val criticalCount by viewModel.criticalCount.collectAsStateWithLifecycle()
    val lowStockCount by viewModel.lowStockCount.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val adminImageUri by viewModel.adminImageUri.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val selectedPartState = viewModel.selectedPart.collectAsStateWithLifecycle()
    val selectedPart = selectedPartState.value
    
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

    selectedPart?.let { part ->
        PartDetailDialog(
            part = part,
            onDismiss = { viewModel.clearSelectedPart() },
            onUpdate = { updatedPart ->
                viewModel.updatePart(updatedPart)
                viewModel.clearSelectedPart()
            },
            onDelete = {
                viewModel.deletePart(part)
                viewModel.clearSelectedPart()
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
                onCalendarClick = {
                    scope.launch { drawerState.close() }
                    onCalendarClick()
                },
                onServicesClick = {
                    scope.launch { drawerState.close() }
                    onServicesClick()
                },
                onProfileClick = {
                    scope.launch { drawerState.close() }
                    onProfileClick()
                },
                onLogout = onLogout,
                onClose = {
                    scope.launch { drawerState.close() }
                },
                currentRoute = Screen.AdminInventory.route,
                userName = userName,
                userImageUri = adminImageUri
            )
        }
    ) {
        Scaffold(
            topBar = { 
                AdminTopBar(
                    title = "INVENTORY",
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                    onProfileClick = onProfileClick,
                    onCalendarClick = onCalendarClick,
                    userImageUri = adminImageUri
                ) 
            },
            bottomBar = { 
                AdminBottomNavigation(
                    onDashboardClick = onHomeClick,
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
                            totalCount = totalCount,
                            criticalCount = criticalCount, 
                            lowStockCount = lowStockCount
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
                            categories = categories,
                            selectedCategory = selectedCategory,
                            onCategorySelect = { viewModel.onCategorySelect(it) }
                        ) 
                    }
                    
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                    
                    items(parts) { part ->
                        InventoryItemCard(
                            status = when {
                                part.stockQuantity == 0 -> "OUT OF STOCK"
                                part.stockQuantity < 5 -> "CRITICAL STOCK"
                                else -> "OPTIMAL"
                            },
                            statusColor = when {
                                part.stockQuantity == 0 -> CriticalRed
                                part.stockQuantity < 5 -> WarningOrange
                                else -> OptimalGrey
                            },
                            title = part.name,
                            sku = part.sku,
                            category = part.category,
                            price = part.price,
                            unitsLeft = part.stockQuantity,
                            minRequired = 5,
                            actionText = if (part.stockQuantity < 5) "RESTOCK +10" else "VIEW DETAILS",
                            isOptimal = part.stockQuantity >= 5,
                            onActionClick = {
                                if (part.stockQuantity < 5) {
                                    viewModel.restockPart(part, 10)
                                } else {
                                    viewModel.onViewPartDetails(part)
                                }
                            },
                            onLongClick = {
                                viewModel.onViewPartDetails(part)
                            }
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
fun InventoryFilters(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        categories.forEach { category ->
            val isSelected = selectedCategory == category
            Surface(
                color = if (isSelected) Primary else Color.White,
                modifier = Modifier
                    .widthIn(min = 80.dp)
                    .clickable { onCategorySelect(category) },
                border = BorderStroke(2.dp, Color.Black),
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    category,
                    color = if (isSelected) Color.White else Neutral,
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InventoryItemCard(
    status: String,
    statusColor: Color,
    title: String,
    sku: String,
    category: String,
    price: Double,
    unitsLeft: Int,
    minRequired: Int,
    actionText: String,
    isOptimal: Boolean = false,
    onActionClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onActionClick,
                onLongClick = onLongClick
            ),
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
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                category.uppercase(),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 24.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "SKU: $sku",
                            fontSize = 10.sp,
                            color = Neutral,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "PRICE: $${String.format(Locale.getDefault(), "%.2f", price)}",
                            fontSize = 10.sp,
                            color = Primary,
                            fontWeight = FontWeight.Black
                        )
                    }
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
                        onClick = onActionClick,
                        shape = RoundedCornerShape(0.dp),
                        border = BorderStroke(2.dp, Color.Black)
                    ) {
                        Text(actionText, color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                } else {
                    Button(
                        onClick = onActionClick,
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
fun PartDetailDialog(
    part: com.example.mad_final.domain.models.Part,
    onDismiss: () -> Unit,
    onUpdate: (com.example.mad_final.domain.models.Part) -> Unit,
    onDelete: () -> Unit
) {
    var name by remember { mutableStateOf(part.name) }
    var sku by remember { mutableStateOf(part.sku) }
    var price by remember { mutableStateOf(part.price.toString()) }
    var category by remember { mutableStateOf(part.category) }
    var stockInput by remember { mutableStateOf(part.stockQuantity.toString()) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    val categories = listOf("ENGINE", "TIRES", "FLUIDS", "BRAKES", "ELECTRICAL", "ACCESSORIES")
    var expanded by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("CONFIRM DELETE", fontWeight = FontWeight.Black) },
            text = { Text("Are you sure you want to remove this SKU from inventory?") },
            confirmButton = {
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = CriticalRed),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text("DELETE", color = Color.White, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("CANCEL", color = Neutral, fontWeight = FontWeight.Black)
                }
            },
            shape = RoundedCornerShape(0.dp),
            containerColor = Color.White
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("EDIT PART DETAILS", fontWeight = FontWeight.Black)
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CriticalRed)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("NAME", fontSize = 10.sp, fontWeight = FontWeight.Black) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp)
                )
                
                OutlinedTextField(
                    value = sku,
                    onValueChange = { sku = it },
                    label = { Text("SKU", fontSize = 10.sp, fontWeight = FontWeight.Black) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("CATEGORY", fontSize = 10.sp, fontWeight = FontWeight.Black) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(0.dp),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    category = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("UNIT PRICE", fontSize = 10.sp, fontWeight = FontWeight.Black) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(0.dp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                        )
                    )
                    OutlinedTextField(
                        value = stockInput,
                        onValueChange = { stockInput = it },
                        label = { Text("STOCK QTY", fontSize = 10.sp, fontWeight = FontWeight.Black) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(0.dp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                }

                if (part.lastRestocked > 0) {
                    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                    val date = Date(part.lastRestocked)
                    Text(
                        "LAST RESTOCKED: ${sdf.format(date)}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Neutral
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedPart = part.copy(
                        name = name,
                        sku = sku,
                        price = price.toDoubleOrNull() ?: part.price,
                        category = category,
                        stockQuantity = stockInput.toIntOrNull() ?: part.stockQuantity
                    )
                    onUpdate(updatedPart)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(0.dp)
            ) {
                Text("SAVE CHANGES", fontWeight = FontWeight.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CLOSE", color = Neutral, fontWeight = FontWeight.Black)
            }
        },
        shape = RoundedCornerShape(0.dp),
        containerColor = Color.White
    )
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
    
    val categories = listOf("ENGINE", "TIRES", "FLUIDS", "BRAKES", "ELECTRICAL", "ACCESSORIES")
    var expanded by remember { mutableStateOf(false) }

    val isInputValid = name.isNotBlank() && 
                       sku.isNotBlank() && 
                       quantity.toIntOrNull() != null && 
                       price.toDoubleOrNull() != null &&
                       category.isNotBlank()

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
                    shape = RoundedCornerShape(0.dp),
                    isError = name.isBlank() && name.isNotEmpty()
                )
                OutlinedTextField(
                    value = sku,
                    onValueChange = { sku = it },
                    label = { Text("SKU") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp),
                    isError = sku.isBlank() && sku.isNotEmpty()
                )
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(0.dp),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    category = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Initial Stock") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp),
                    isError = quantity.isNotEmpty() && quantity.toIntOrNull() == null
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Unit Price") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp),
                    isError = price.isNotEmpty() && price.toDoubleOrNull() == null
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
                enabled = isInputValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    disabledContainerColor = Primary.copy(alpha = 0.5f)
                ),
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
