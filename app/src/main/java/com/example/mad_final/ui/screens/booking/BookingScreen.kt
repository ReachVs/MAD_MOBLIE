package com.example.mad_final.ui.screens.booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.mad_final.domain.models.WorkshopService
import com.example.mad_final.ui.components.TechnicalGridBackground
import com.example.mad_final.ui.theme.Background
import com.example.mad_final.ui.theme.Neutral
import com.example.mad_final.ui.theme.Primary
import com.example.mad_final.ui.theme.Secondary
import com.example.mad_final.ui.screens.catalog.MainCategoryCard
import com.example.mad_final.ui.screens.catalog.SubCategoryCard
import com.example.mad_final.ui.screens.catalog.ServiceListItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.tooling.preview.Preview
import com.example.mad_final.ui.theme.ApexMotorworksTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    onBookingFinished: () -> Unit,
    onBack: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedServices = remember(uiState.services, uiState.selectedServiceIds) {
        uiState.services.filter { uiState.selectedServiceIds.contains(it.id) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "MAD APE",
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp,
                        fontSize = 24.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        AsyncImage(
                            model = uiState.userImageUri,
                            contentDescription = "User Profile",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color.Black, CircleShape),
                            contentScale = ContentScale.Crop,
                            placeholder = rememberVectorPainter(Icons.Default.Person),
                            error = rememberVectorPainter(Icons.Default.Person)
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
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Background)
        ) {
            TechnicalGridBackground()
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                BookingStepIndicator(
                    step = uiState.step,
                    isComingFromCatalog = uiState.isFromCatalog
                )

                when (uiState.step) {
                    1 -> MotorcycleSelectionStep(
                        manufacturer = uiState.manufacturer,
                        model = uiState.model,
                        year = uiState.year,
                        onUpdateManufacturer = viewModel::updateManufacturer,
                        onUpdateModel = viewModel::updateModel,
                        onUpdateYear = viewModel::updateYear,
                        onNext = viewModel::nextStep,
                        onBack = onBack,
                        isComingFromCatalog = uiState.isFromCatalog,
                        step = uiState.step,
                        isValid = uiState.isMotorcycleValid
                    )
                    2 -> if (uiState.isFromCatalog) {
                        ScheduleStep(
                            selectedDate = uiState.selectedDate,
                            selectedTime = uiState.selectedTime,
                            onDateSelected = viewModel::updateSelectedDate,
                            onTimeSelected = viewModel::updateSelectedTime,
                            onBack = viewModel::previousStep,
                            onNext = viewModel::nextStep,
                            isComingFromCatalog = uiState.isFromCatalog
                        )
                    } else {
                        ServiceSelectionStep(
                            serviceIntent = uiState.serviceIntent,
                            availableServices = uiState.services,
                            selectedServiceIds = uiState.selectedServiceIds,
                            onToggleService = viewModel::toggleService,
                            onNext = viewModel::nextStep,
                            isComingFromCatalog = uiState.isFromCatalog,
                            onBack = viewModel::previousStep,
                            step = uiState.step,
                            isValid = uiState.isServiceValid
                        )
                    }
                    3 -> if (uiState.isFromCatalog) {
                        FinalPaymentStep(
                            selectedServices = selectedServices,
                            totalPrice = uiState.totalPrice,
                            onBack = viewModel::previousStep,
                            onConfirm = viewModel::confirmBooking,
                            onRemoveService = viewModel::toggleService
                        )
                    } else {
                        ScheduleStep(
                            selectedDate = uiState.selectedDate,
                            selectedTime = uiState.selectedTime,
                            onDateSelected = viewModel::updateSelectedDate,
                            onTimeSelected = viewModel::updateSelectedTime,
                            onBack = viewModel::previousStep,
                            onNext = viewModel::nextStep,
                            isComingFromCatalog = uiState.isFromCatalog
                        )
                    }
                    4 -> if (uiState.isFromCatalog) {
                        BookingConfirmationStep(onDone = onBookingFinished)
                    } else {
                        FinalPaymentStep(
                            selectedServices = selectedServices,
                            totalPrice = uiState.totalPrice,
                            onBack = viewModel::previousStep,
                            onConfirm = viewModel::confirmBooking,
                            onRemoveService = viewModel::toggleService
                        )
                    }
                    5 -> BookingConfirmationStep(onDone = onBookingFinished)
                }
            }
        }
    }
}

@Composable
private fun BookingStepIndicator(step: Int, isComingFromCatalog: Boolean) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            val totalSteps = if (isComingFromCatalog) 3 else 4
            Text(
                if (step <= totalSteps) "STEP 0$step / 0$totalSteps" else "COMPLETE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Primary,
                letterSpacing = 0.5.sp
            )
            Text(
                getStepTitle(step, isComingFromCatalog).uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Primary,
                letterSpacing = 0.5.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color.Black.copy(alpha = 0.1f))) {
            val progress = if (isComingFromCatalog) step / 4f else step / 5f
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(Secondary)
            )
        }
    }
}

private fun getStepTitle(step: Int, isComingFromCatalog: Boolean) = when {
    step == 1 -> "MACHINE IDENTIFICATION"
    step == 2 && isComingFromCatalog -> "SELECT SCHEDULE"
    step == 2 && !isComingFromCatalog -> "SERVICE SELECTION"
    step == 3 && isComingFromCatalog -> "PAYMENT"
    step == 3 && !isComingFromCatalog -> "SELECT SCHEDULE"
    step == 4 && !isComingFromCatalog -> "PAYMENT"
    else -> "CONFIRMATION"
}

@Composable
fun ServiceSelectionStep(
    serviceIntent: String,
    availableServices: List<WorkshopService>,
    selectedServiceIds: Set<String>,
    onToggleService: (String) -> Unit,
    onNext: () -> Unit,
    isComingFromCatalog: Boolean,
    onBack: () -> Unit,
    step: Int,
    isValid: Boolean
) {
    val expandedCategories = remember { mutableStateMapOf<String, Boolean>() }
    val expandedSubCategories = remember { mutableStateMapOf<String, Boolean>() }
    
    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            "SERVICE\nINTEGRATION",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            lineHeight = 36.sp,
            letterSpacing = (-1).sp
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Select the primary engineering intent for your machine's diagnostic session.",
            fontSize = 14.sp,
            color = Neutral,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // --- NEW: Restyled Selected Services Summary (CRUD) ---
        if (selectedServiceIds.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .background(Color.Black.copy(alpha = 0.03f), RoundedCornerShape(0.dp))
                    .border(1.dp, Color.Black.copy(alpha = 0.1f), RoundedCornerShape(0.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "CURRENT SELECTION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        letterSpacing = 1.sp
                    )
                    Surface(
                        color = Color.Black,
                        shape = CircleShape,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                selectedServiceIds.size.toString(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                val selectedServices = availableServices.filter { selectedServiceIds.contains(it.id) }
                selectedServices.forEach { service ->
                    ServiceSummaryItem(
                        service = service,
                        onRemove = { onToggleService(service.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Text("PRIMARY SERVICE INTENT", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Primary)
        Spacer(modifier = Modifier.height(16.dp))
        
        // Group services for nested display
        val groupedByMain = availableServices.groupBy { it.category }
        
        if (availableServices.isEmpty()) {
            // Fallback to static if empty
            val displayIntents = listOf(
                Pair("MAINTENANCE SERVICES", "Precision fluid & filter technical service."),
                Pair("WASHING", "Professional deep cleaning and aesthetic maintenance."),
                Pair("ENGINE CHECK UP", "Complete core system diagnostic and verification."),
                Pair("TUNING PERFORMANCE", "Optimal mapping & ECU calibration.")
            )
            
            displayIntents.forEach { (title, desc) ->
                TechnicalIntentCard(
                    title = title,
                    description = desc,
                    icon = Icons.Default.Build,
                    isSelected = serviceIntent.equals(title, ignoreCase = true),
                    onClick = { /* onServiceIntentChange(title) */ }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        } else {
            groupedByMain.forEach { (category, categoryServices) ->
                val isExpanded = expandedCategories[category] ?: false
                
                MainCategoryCard(
                    category = category,
                    imageUrl = categoryServices.firstOrNull()?.imageUrl ?: "",
                    isExpanded = isExpanded,
                    onExpandToggle = { expandedCategories[category] = !isExpanded }
                ) {
                    val groupedBySub = categoryServices.groupBy { it.subCategory ?: "GENERAL SERVICES" }
                    
                    groupedBySub.forEach { (subCategory, subServices) ->
                        val subKey = "$category-$subCategory"
                        val isSubExpanded = expandedSubCategories[subKey] ?: false
                        
                        SubCategoryCard(
                            subCategory = subCategory,
                            isExpanded = isSubExpanded,
                            onExpandToggle = { expandedSubCategories[subKey] = !isSubExpanded }
                        ) {
                            subServices.forEach { serviceItem ->
                                ServiceListItem(
                                    service = serviceItem,
                                    isSelected = selectedServiceIds.contains(serviceItem.id),
                                    onToggle = { onToggleService(serviceItem.id) }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Info Box
        Surface(
            color = Color(0xFFF8F8F8),
            border = BorderStroke(2.dp, Color.Black),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Secondary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Selected intent determines the technical specialist assigned to your machine.",
                    fontSize = 12.sp,
                    color = Neutral,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            enabled = isValid,
            border = BorderStroke(2.dp, Color.Black)
        ) {
            Text(
                if (isComingFromCatalog) "PROCEED TO MACHINE DATA" else "PROCEED TO SCHEDULING",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }
        
        if (step > 1) {
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text(
                    if (isComingFromCatalog) "BACK TO SERVICE SELECTION" else "BACK TO MACHINE DATA",
                    color = Neutral,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotorcycleSelectionStep(
    manufacturer: String,
    model: String,
    year: String,
    onUpdateManufacturer: (String) -> Unit,
    onUpdateModel: (String) -> Unit,
    onUpdateYear: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    isComingFromCatalog: Boolean,
    step: Int,
    isValid: Boolean
) {
    val manufacturers = listOf("DUCATI", "BMW", "YAMAHA", "HONDA", "KAWASAKI", "TRIUMPH", "APRILIA", "KTM", "SUZUKI", "HARLEY-DAVIDSON")
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            "MACHINE\nIDENTIFICATION",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            lineHeight = 36.sp,
            letterSpacing = (-1).sp
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Enter the technical specifications for the unit requiring engineering attention.",
            fontSize = 14.sp,
            color = Neutral,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold
        )
        

        Spacer(modifier = Modifier.height(32.dp))
        Text("MANUAL SPECIFICATIONS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Primary)
        Spacer(modifier = Modifier.height(16.dp))

        Text("MANUFACTURER", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Primary, letterSpacing = 1.sp)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = manufacturer,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("SELECT BRAND", color = Color.LightGray, fontSize = 18.sp, fontWeight = FontWeight.Black) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .border(2.dp, Color.Black),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    cursorColor = Primary
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.Black, color = Primary)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                manufacturers.forEach { brand ->
                    DropdownMenuItem(
                        text = { Text(brand, fontWeight = FontWeight.Black) },
                        onClick = {
                            onUpdateManufacturer(brand)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        TechnicalInputField(
            label = "MODEL / SERIES",
            value = model,
            onValueChange = onUpdateModel,
            placeholder = "e.g. PANIGALE V4"
        )
        Spacer(modifier = Modifier.height(16.dp))
        TechnicalInputField(
            label = "PRODUCTION YEAR",
            value = year,
            onValueChange = onUpdateYear,
            placeholder = "e.g. 2024"
        )

        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            enabled = isValid,
            border = BorderStroke(2.dp, Color.Black)
        ) {
            Text(
                if (isComingFromCatalog) "PROCEED TO SCHEDULING" else "PROCEED TO SERVICE SELECTION",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }

        if (step > 1) {
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text(
                    if (isComingFromCatalog) "BACK TO SERVICE SELECTION" else "BACK TO MACHINE DATA",
                    color = Neutral,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun TechnicalInputField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Column {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Black, color = Primary, letterSpacing = 1.sp)
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray, fontSize = 18.sp, fontWeight = FontWeight.Black) },
            modifier = Modifier.fillMaxWidth().border(2.dp, Color.Black),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = Primary
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.Black, color = Primary)
        )
    }
}

@Composable
fun TechnicalIntentCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = if (isSelected) Color(0xFFF1F5F9) else Color.White,
        border = BorderStroke(
            width = 2.dp,
            color = if (isSelected) Primary else Color.Black
        ),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                color = if (isSelected) Primary else Color(0xFFF1F5F9),
                shape = RoundedCornerShape(0.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else Primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = Neutral,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Secondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleStep(
    selectedDate: Long?,
    selectedTime: String,
    onDateSelected: (Long?) -> Unit,
    onTimeSelected: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    isComingFromCatalog: Boolean
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate ?: System.currentTimeMillis()
    )
    
    // Sync picker state with viewmodel
    LaunchedEffect(datePickerState.selectedDateMillis) {
        if (datePickerState.selectedDateMillis != null) {
            onDateSelected(datePickerState.selectedDateMillis)
        }
    }

    val timeSlots = listOf(
        "09:00 AM", "10:00 AM", "11:00 AM", 
        "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM"
    )

    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            "SELECT SERVICE\nWINDOW",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            lineHeight = 36.sp,
            letterSpacing = (-1).sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Choose your preferred engineering slot. Our team will verify parts availability for this date.",
            fontSize = 14.sp,
            color = Neutral,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        Text("SELECT DATE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Primary, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(12.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(2.dp, Color.Black),
            color = Color.White,
            shape = RoundedCornerShape(0.dp)
        ) {
            DatePicker(
                state = datePickerState,
                title = null,
                headline = null,
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White,
                    selectedDayContainerColor = Secondary,
                    selectedDayContentColor = Color.White,
                    todayContentColor = Secondary,
                    todayDateBorderColor = Secondary
                ),
                modifier = Modifier.scale(0.85f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Text("SELECT TIME SLOT", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Primary, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(12.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Display time slots in rows of 3
            timeSlots.chunked(3).forEach { rowSlots ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowSlots.forEach { time ->
                        val isSelected = selectedTime == time
                        Surface(
                            onClick = { onTimeSelected(time) },
                            modifier = Modifier.weight(1f).height(44.dp),
                            color = if (isSelected) Color.Black else Color.White,
                            border = BorderStroke(2.dp, Color.Black),
                            shape = RoundedCornerShape(0.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    time,
                                    color = if (isSelected) Color.White else Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                    // Fill empty space if row is not full
                    repeat(3 - rowSlots.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        val formattedDate = remember(selectedDate) {
            selectedDate?.let {
                SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(it))
            } ?: "None selected"
        }

        TechnicalDataRow(
            label = "CONFIRMED ARRIVAL",
            value = "${formattedDate.uppercase()} @ $selectedTime",
            icon = Icons.Default.DateRange
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            enabled = selectedDate != null,
            border = BorderStroke(2.dp, Color.Black)
        ) {
            Text("PROCEED TO AUTHORIZATION", fontWeight = FontWeight.Black, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text(
                if (isComingFromCatalog) "BACK TO MACHINE DATA" else "BACK TO SERVICE SELECTION",
                color = Neutral,
                fontWeight = FontWeight.Black,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun FinalPaymentStep(
    selectedServices: List<WorkshopService>,
    totalPrice: Double,
    onBack: () -> Unit, 
    onConfirm: () -> Unit,
    onRemoveService: (String) -> Unit
) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text("FINAL PAYMENT\nAUTHORIZATION", fontSize = 32.sp, fontWeight = FontWeight.Black, lineHeight = 36.sp)
        Spacer(modifier = Modifier.height(32.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(2.dp, Color.Black),
            color = Color.White,
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("ENGINEERING FEE SUMMARY", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Neutral)
                Spacer(modifier = Modifier.height(24.dp))
                
                selectedServices.forEach { service ->
                    ServiceSummaryItem(
                        service = service,
                        onRemove = { onRemoveService(service.id) },
                        isDark = false
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                if (selectedServices.isEmpty()) {
                    Text("NO SERVICES SELECTED", fontWeight = FontWeight.Black, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TECH DIAGNOSTIC FEE", fontWeight = FontWeight.Black, fontSize = 12.sp)
                    Text("$0.00", fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(thickness = 2.dp, color = Color.Black)
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TOTAL (USD)", fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Text("$${String.format(Locale.US, "%.2f", totalPrice)}", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Secondary)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(64.dp))
        
        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Secondary),
            border = BorderStroke(2.dp, Color.Black),
            enabled = selectedServices.isNotEmpty()
        ) {
            Text("CONFIRM BOOKING", fontWeight = FontWeight.Black, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("BACK TO SCHEDULE", color = Neutral, fontWeight = FontWeight.Black, fontSize = 10.sp)
        }
    }
}

@Composable
fun BookingConfirmationStep(onDone: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(80.dp), tint = Primary)
        Spacer(modifier = Modifier.height(32.dp))
        Text("SERVICE LOGGED", fontSize = 32.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Your machine has been queued for engineering review. Transaction #MA-2024-8842",
            textAlign = TextAlign.Center,
            color = Neutral,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(64.dp))
        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            border = BorderStroke(2.dp, Color.Black)
        ) {
            Text("RETURN TO HUB", fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun ServiceSummaryItem(
    service: WorkshopService,
    onRemove: () -> Unit,
    isDark: Boolean = true
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDark) Color.White else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.Black, RoundedCornerShape(0.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Build,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        service.title.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = Primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Text(
                            service.category.uppercase(),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }
                }
                Text(
                    "$${service.price}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Secondary
                )
            }
            
            IconButton(
                onClick = onRemove,
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

@Preview(showBackground = true, name = "Manual Path - Machine Info")
@Composable
fun PreviewBookingManualStep1() {
    ApexMotorworksTheme {
        Box(modifier = Modifier.background(Background)) {
            TechnicalGridBackground()
            MotorcycleSelectionStep(
                manufacturer = "Yamaha",
                model = "R1",
                year = "2024",
                onUpdateManufacturer = {},
                onUpdateModel = {},
                onUpdateYear = {},
                onNext = {},
                onBack = {},
                isComingFromCatalog = false,
                step = 1,
                isValid = true
            )
        }
    }
}

@Preview(showBackground = true, name = "Catalog Path - Schedule")
@Composable
fun PreviewBookingCatalogStep2() {
    ApexMotorworksTheme {
        Box(modifier = Modifier.background(Background)) {
            TechnicalGridBackground()
            ScheduleStep(
                selectedDate = System.currentTimeMillis(),
                selectedTime = "10:00 AM",
                onDateSelected = {},
                onTimeSelected = {},
                onBack = {},
                onNext = {},
                isComingFromCatalog = true
            )
        }
    }
}

@Preview(showBackground = true, name = "Payment Summary")
@Composable
fun PreviewBookingPayment() {
    ApexMotorworksTheme {
        Box(modifier = Modifier.background(Background)) {
            TechnicalGridBackground()
            FinalPaymentStep(
                selectedServices = listOf(
                    WorkshopService("1", "Full Engine Service", 150.00, "2h", "", "", emptyList(), "Maintenance"),
                    WorkshopService("2", "Chain Tensioning", 25.00, "20m", "", "", emptyList(), "Maintenance")
                ),
                totalPrice = 175.0,
                onBack = {},
                onConfirm = {},
                onRemoveService = {}
            )
        }
    }
}

@Composable
fun TechnicalDataRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(2.dp, Color.Black),
        color = Color.White,
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = Primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, fontSize = 9.sp, fontWeight = FontWeight.Black, color = Neutral)
                Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}
