package com.example.mad_final.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mad_final.domain.models.WorkshopService
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import com.example.mad_final.ui.components.TechnicalGridBackground
import com.example.mad_final.ui.theme.Primary
import com.example.mad_final.ui.theme.Secondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminServiceManagementScreen(
    viewModel: AdminServiceViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val services by viewModel.services.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var serviceToEdit by remember { mutableStateOf<WorkshopService?>(null) }
    var serviceToDelete by remember { mutableStateOf<WorkshopService?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (uiState) {
            is ServiceUiState.Success -> {
                snackbarHostState.showSnackbar((uiState as ServiceUiState.Success).message)
                viewModel.resetState()
            }
            is ServiceUiState.Error -> {
                snackbarHostState.showSnackbar((uiState as ServiceUiState.Error).message)
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SERVICE MANAGEMENT", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Service")
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            TechnicalGridBackground()
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(services) { service ->
                    ServiceItem(
                        service = service,
                        onEdit = { serviceToEdit = service },
                        onDelete = { serviceToDelete = service }
                    )
                }
            }
        }

        if (showAddDialog) {
            ServiceDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { title, price, duration, description, imageUrl, category ->
                    viewModel.addService(title, price, duration, description, imageUrl, category)
                    showAddDialog = false
                }
            )
        }

        serviceToEdit?.let { service ->
            ServiceDialog(
                service = service,
                onDismiss = { serviceToEdit = null },
                onConfirm = { title, price, duration, description, imageUrl, category ->
                    viewModel.updateService(service.copy(
                        title = title,
                        price = price,
                        duration = duration,
                        description = description,
                        imageUrl = imageUrl,
                        category = category
                    ))
                    serviceToEdit = null
                }
            )
        }

        serviceToDelete?.let { service ->
            AlertDialog(
                onDismissRequest = { serviceToDelete = null },
                title = { Text("Delete Service", fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to delete '${service.title}'?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteService(service)
                            serviceToDelete = null
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Secondary)
                    ) {
                        Text("DELETE", fontWeight = FontWeight.Black)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { serviceToDelete = null }) {
                        Text("CANCEL")
                    }
                }
            )
        }
    }
}

@Composable
fun ServiceItem(
    service: WorkshopService,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.Black, RoundedCornerShape(0.dp)),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    service.title.uppercase(),
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                Text(
                    service.category,
                    color = Primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Price: $${String.format(java.util.Locale.US, "%.2f", service.price)} • Duration: ${service.duration}",
                    fontSize = 12.sp
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Secondary)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDialog(
    service: WorkshopService? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, String, String, String) -> Unit
) {
    val categories = listOf(
        "MAINTENANCE SERVICES",
        "WASHING",
        "ENGINE CHECK UP",
        "TUNING PERFORMANCE"
    )

    var title by remember { mutableStateOf(service?.title ?: "") }
    var price by remember { mutableStateOf(service?.price?.toString() ?: "") }
    var duration by remember { mutableStateOf(service?.duration ?: "") }
    var description by remember { mutableStateOf(service?.description ?: "") }
    var imageUrl by remember { mutableStateOf(service?.imageUrl ?: "") }
    var category by remember { mutableStateOf(service?.category ?: categories[0]) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                if (service == null) "ADD SERVICE" else "EDIT SERVICE",
                fontWeight = FontWeight.Black
            ) 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price (e.g. 50.00)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (e.g. 2h)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Restricted Category Selection
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
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
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Image URL") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    val priceDouble = price.toDoubleOrNull()
                    if (title.isNotBlank() && priceDouble != null) {
                        onConfirm(title, priceDouble, duration, description, imageUrl, category)
                    }
                },
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("SAVE", fontWeight = FontWeight.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = Color.Black)
            }
        }
    )
}
