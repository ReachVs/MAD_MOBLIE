package com.example.mad_final.ui.screens.detail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

import com.example.mad_final.ui.theme.Primary
import com.example.mad_final.ui.theme.Secondary
import com.example.mad_final.ui.theme.Neutral
import com.example.mad_final.ui.theme.Background

private val GridColor = Color(0xFFE2E8F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    onBackClick: () -> Unit,
    onBookClick: (String, String) -> Unit,
    viewModel: ServiceDetailViewModel = hiltViewModel()
) {
    val service by viewModel.service.collectAsState()
    val selectedCapacity by viewModel.selectedCapacity.collectAsState()

    if (service == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Secondary)
        }
        return
    }

    val svc = service!!
    val context = androidx.compose.ui.platform.LocalContext.current
    val imageModel = remember(svc.imageUrl) {
        val url = svc.imageUrl.trim()
        if (url.startsWith("http")) {
            url
        } else if (url.isNotEmpty()) {
            val resId = context.resources.getIdentifier(url, "drawable", context.packageName)
            if (resId != 0) resId else null
        } else {
            null
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SERVICE SPECIFICATIONS", fontWeight = FontWeight.Black, letterSpacing = 2.sp, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Background)
        ) {
            com.example.mad_final.ui.components.TechnicalGridBackground()
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                val placeholderPainter = rememberVectorPainter(Icons.Default.Refresh)
                val errorPainter = rememberVectorPainter(Icons.Default.Warning)
                
                AsyncImage(
                    model = imageModel,
                    contentDescription = "Image for ${svc.title}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = placeholderPainter,
                    error = errorPainter,
                    onLoading = { android.util.Log.d("ServiceDetail", "Loading image: $imageModel") },
                    onError = { android.util.Log.e("ServiceDetail", "Error loading image: $imageModel", it.result.throwable) }
                )

                Column(modifier = Modifier.padding(24.dp)) {
                    Surface(
                        color = Secondary,
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Text(
                            svc.category.uppercase(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        svc.title.uppercase(),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 38.sp,
                        letterSpacing = (-1).sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        svc.price,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(4.dp)
                            .background(Color.Black)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "TECHNICAL OVERVIEW",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Secondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        svc.description,
                        fontSize = 16.sp,
                        color = Neutral,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    if (svc.category == "PERFORMANCE") {
                        Text(
                            "ENGINE CAPACITY",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val capacities = listOf("1000cc", "600cc", "( 400/200cc )")
                            capacities.forEach { capacity ->
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clickable { viewModel.selectCapacity(capacity) },
                                    color = if (selectedCapacity == capacity) Primary else Color.White,
                                    border = BorderStroke(2.dp, Color.Black),
                                    shape = RoundedCornerShape(0.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            capacity.uppercase(),
                                            color = if (selectedCapacity == capacity) Color.White else Neutral,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                    
                    Text(
                        "CONFIGURATION OPTIONS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OptionRow("Standard Diagnostics", true)
                    OptionRow("OEM Parts Only", false)
                    OptionRow("Performance Optimization", svc.category == "PERFORMANCE")
                    
                    Spacer(modifier = Modifier.height(48.dp))

                    Button(
                        onClick = { onBookClick(svc.id, selectedCapacity) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary),
                        border = BorderStroke(2.dp, Color.Black)
                    ) {
                        Text("PROCEED TO BOOKING", fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun OptionRow(label: String, included: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Black, color = Primary)
        if (included) {
            Text("INCLUDED", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF15803D))
        } else {
            Text("OPTIONAL", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Neutral)
        }
    }
    HorizontalDivider(thickness = 1.dp, color = Color.Black.copy(alpha = 0.1f))
}
