package com.example.mad_final.ui.screens.booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import coil.compose.AsyncImage
import com.example.mad_final.ui.components.TechnicalGridBackground
import com.example.mad_final.ui.theme.Background
import com.example.mad_final.ui.theme.Neutral
import com.example.mad_final.ui.theme.Primary
import com.example.mad_final.ui.theme.Secondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val OptimalGreen = Color(0xFF15803D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    onBookingFinished: () -> Unit,
    onBack: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val step by viewModel.bookingStep.collectAsState()
    val manufacturer by viewModel.manufacturer.collectAsState()
    val model by viewModel.model.collectAsState()
    val year by viewModel.year.collectAsState()
    val engineCapacity by viewModel.engineCapacity.collectAsState()
    val serviceIntent by viewModel.serviceIntent.collectAsState()
    val configuration by viewModel.configuration.collectAsState()
    val service by viewModel.service.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()
    val availableServices by viewModel.services.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

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
                    IconButton(onClick = {}) {
                        val placeholderPainter = rememberVectorPainter(Icons.Default.Refresh)
                        val errorPainter = rememberVectorPainter(Icons.Default.Warning)
                        
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?q=80&w=100&auto=format&fit=crop",
                            contentDescription = "User Profile",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(0.dp))
                                .border(1.dp, Color.Black),
                            contentScale = ContentScale.Crop,
                            placeholder = placeholderPainter,
                            error = errorPainter,
                            onLoading = { android.util.Log.d("BookingScreen", "Loading profile image") },
                            onError = { android.util.Log.e("BookingScreen", "Error loading profile image", it.result.throwable) }
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
                // Step Indicator Header
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            "STEP 0$step / 05",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Primary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            when(step) {
                                1 -> "SERVICE INTEGRATION"
                                2 -> "VEHICLE SPECS"
                                3 -> "SELECT SCHEDULE"
                                4 -> "PAYMENT"
                                else -> "CONFIRMATION"
                            }.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Primary,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color.Black.copy(alpha = 0.1f))) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(step / 5f)
                                .fillMaxHeight()
                                .background(Secondary)
                        )
                    }
                }

                when (step) {
                    1 -> ServiceSelectionStep(
                        serviceIntent = serviceIntent,
                        availableServices = availableServices,
                        onServiceIntentChange = viewModel::updateServiceIntent,
                        configuration = configuration,
                        selectedService = service,
                        onNext = { viewModel.nextStep() }
                    )
                    2 -> VehicleSpecsStep(
                        manufacturer = manufacturer,
                        onManufacturerChange = viewModel::updateManufacturer,
                        model = model,
                        onModelChange = viewModel::updateModel,
                        year = year,
                        onYearChange = viewModel::updateYear,
                        engineCapacity = engineCapacity,
                        onEngineCapacityChange = viewModel::updateEngineCapacity,
                        onBack = { viewModel.previousStep() },
                        onNext = { viewModel.nextStep() }
                    )
                    3 -> ScheduleStep(
                        selectedDate = selectedDate,
                        onDateSelected = viewModel::updateSelectedDate,
                        onBack = { viewModel.previousStep() },
                        onNext = { viewModel.nextStep() }
                    )
                    4 -> FinalPaymentStep(
                        serviceTitle = service?.title ?: serviceIntent,
                        price = totalPrice,
                        onBack = { viewModel.previousStep() },
                        onConfirm = { viewModel.confirmBooking() }
                    )
                    5 -> BookingConfirmationStep(
                        onDone = onBookingFinished
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceSelectionStep(
    serviceIntent: String,
    availableServices: List<com.example.mad_final.domain.models.WorkshopService>,
    onServiceIntentChange: (String) -> Unit,
    configuration: String,
    selectedService: com.example.mad_final.domain.models.WorkshopService? = null,
    onNext: () -> Unit
) {
    Column(modifier = Modifier.padding(24.dp)) {
        if (selectedService != null) {
            Text(
                selectedService.title.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Secondary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

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
        
        Text("PRIMARY SERVICE INTENT", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Primary)
        Spacer(modifier = Modifier.height(16.dp))
        
        // Show actual services from database/catalog
        if (availableServices.isEmpty()) {
            // Fallback to static if empty
            val displayIntents = listOf(
                Pair("SCHEDULED MAINTENANCE", "Precision fluid \u0026 filter technical service."),
                Pair("ENGINE OVERHAUL", "Complete core system reconstruction."),
                Pair("ELECTRICAL DIAGNOSTICS", "Advanced sensor \u0026 loom verification."),
                Pair("PERFORMANCE TUNING", "Optimal mapping \u0026 ECU calibration.")
            )
            
            displayIntents.forEach { (title, desc) ->
                TechnicalIntentCard(
                    title = title,
                    description = desc,
                    icon = Icons.Default.Build,
                    isSelected = serviceIntent.equals(title, ignoreCase = true),
                    onClick = { onServiceIntentChange(title) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        } else {
            availableServices.forEach { serviceItem ->
                val isSelected = serviceIntent.equals(serviceItem.title, ignoreCase = true)
                val context = androidx.compose.ui.platform.LocalContext.current
                val imageModel = remember(serviceItem.imageUrl) {
                    val url = serviceItem.imageUrl.trim()
                    if (url.startsWith("http")) {
                        url
                    } else if (url.isNotEmpty()) {
                        val resId = context.resources.getIdentifier(url, "drawable", context.packageName)
                        if (resId != 0) resId else null
                    } else {
                        null
                    }
                }
                
                // Show as full technical card to match Catalog Page visual
                val placeholderPainter = rememberVectorPainter(Icons.Default.Refresh)
                val errorPainter = rememberVectorPainter(Icons.Default.Warning)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onServiceIntentChange(serviceItem.title) },
                    border = BorderStroke(
                        width = 2.dp,
                        color = if (isSelected) Primary else Color.Black
                    ),
                    color = Color.White,
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Column {
                        Box {
                            AsyncImage(
                                model = imageModel,
                                contentDescription = "Image for ${serviceItem.title}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp),
                                contentScale = ContentScale.Crop,
                                placeholder = placeholderPainter,
                                error = errorPainter,
                                onLoading = { android.util.Log.d("BookingScreen", "Loading image: $imageModel") },
                                onError = { android.util.Log.e("BookingScreen", "Error loading image: $imageModel", it.result.throwable) }
                            )
                            
                            if (isSelected) {
                                Surface(
                                    color = Secondary,
                                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                                    shape = RoundedCornerShape(0.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.padding(4.dp).size(16.dp)
                                    )
                                }
                            }

                            Surface(
                                color = Secondary,
                                modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                                shape = RoundedCornerShape(0.dp)
                            ) {
                                Text(
                                    serviceItem.category.uppercase(),
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                        
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = serviceItem.title.uppercase(),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = serviceItem.price,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Secondary
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = serviceItem.description,
                                fontSize = 12.sp,
                                color = Neutral,
                                lineHeight = 16.sp,
                                maxLines = 2,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                val isTuningService = serviceItem.title.contains("TUNING", ignoreCase = true) || 
                                     serviceItem.title.contains("PERFORMANCE", ignoreCase = true)
                if (isSelected && isTuningService && configuration.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F5F9))
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text(
                            "SELECTED CONFIG: ${configuration.uppercase()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Secondary,
                            letterSpacing = 0.5.sp
                        )
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
            border = BorderStroke(2.dp, Color.Black)
        ) {
            Text("PROCEED TO VEHICLE SPECS", fontWeight = FontWeight.Black, fontSize = 14.sp)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun VehicleSpecsStep(
    manufacturer: String,
    onManufacturerChange: (String) -> Unit,
    model: String,
    onModelChange: (String) -> Unit,
    year: String,
    onYearChange: (String) -> Unit,
    engineCapacity: String,
    onEngineCapacityChange: (String) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            "TECHNICAL\nSPECIFICATIONS",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            lineHeight = 36.sp,
            letterSpacing = (-1).sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Input precise vehicle data for parts compatibility and engineering verification.",
            fontSize = 14.sp,
            color = Neutral,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(40.dp))
        
        TechnicalInputField(label = "MANUFACTURER", value = manufacturer, onValueChange = onManufacturerChange, placeholder = "e.g. DUCATI, TRIUMPH, BMW")
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                TechnicalInputField(label = "MODEL", value = model, onValueChange = onModelChange, placeholder = "PANIGALE V4")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.weight(0.6f)) {
                TechnicalInputField(label = "YEAR", value = year, onValueChange = onYearChange, placeholder = "2024")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Text("ENGINE CAPACITY", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Primary, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val capacities = listOf("1000CC", "600CC", "( 400/200CC )")
            capacities.forEach { cap ->
                val isSelected = engineCapacity == cap
                Surface(
                    onClick = { onEngineCapacityChange(cap) },
                    modifier = Modifier.weight(1f).height(48.dp),
                    color = if (isSelected) Primary else Color.White,
                    border = BorderStroke(2.dp, Color.Black),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            cap.uppercase(),
                            color = if (isSelected) Color.White else Neutral,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(64.dp))
        
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            border = BorderStroke(2.dp, Color.Black)
        ) {
            Text("NEXT: SELECT SCHEDULE", fontWeight = FontWeight.Black, fontSize = 14.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("BACK TO SERVICE SELECTION", color = Neutral, fontWeight = FontWeight.Black, fontSize = 10.sp)
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
    onDateSelected: (Long?) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate ?: System.currentTimeMillis()
    )
    
    // Sync picker state with viewmodel
    LaunchedEffect(datePickerState.selectedDateMillis) {
        onDateSelected(datePickerState.selectedDateMillis)
    }

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

        // Material 3 Date Picker Integration
        Surface(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(2.dp, Color.Black),
            color = Color.White,
            shape = RoundedCornerShape(0.dp)
        ) {
            Column {
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
                    modifier = Modifier.scale(0.9f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        val formattedDate = remember(selectedDate) {
            selectedDate?.let {
                SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(it))
            } ?: "None selected"
        }

        TechnicalDataRow(
            label = "CONFIRMED ARRIVAL",
            value = formattedDate.uppercase(),
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
            Text("BACK TO VEHICLE SPECS", color = Neutral, fontWeight = FontWeight.Black, fontSize = 10.sp)
        }
    }
}

@Composable
fun FinalPaymentStep(
    serviceTitle: String,
    price: Double,
    onBack: () -> Unit, 
    onConfirm: () -> Unit
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
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(serviceTitle.uppercase(), fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                    Text("$${String.format("%.2f", price)}", fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TECH DIAGNOSTIC FEE", fontWeight = FontWeight.Black)
                    Text("$0.00", fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(thickness = 2.dp, color = Color.Black)
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TOTAL (USD)", fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Text("$${String.format("%.2f", price)}", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Secondary)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(64.dp))
        
        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Secondary),
            border = BorderStroke(2.dp, Color.Black)
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
        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(80.dp), tint = OptimalGreen)
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
