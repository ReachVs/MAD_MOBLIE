package com.example.mad_final.ui.screens.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage

import com.example.mad_final.ui.theme.Primary
import com.example.mad_final.ui.theme.Secondary
import com.example.mad_final.ui.theme.Neutral
import com.example.mad_final.ui.theme.Background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotorcycleDetailScreen(
    onBackClick: () -> Unit,
    onBookClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    viewModel: MotorcycleDetailViewModel = hiltViewModel()
) {
    val motorcycle by viewModel.motorcycle.collectAsStateWithLifecycle()
    val userImageUri by viewModel.userImageUri.collectAsStateWithLifecycle()

    if (motorcycle == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Secondary)
        }
        return
    }

    val moto = motorcycle!!
    val context = androidx.compose.ui.platform.LocalContext.current
    val imageModel = remember(moto.imageUrl) {
        val url = moto.imageUrl.trim()
        if (url.startsWith("http")) {
            url
        } else if (url.isNotEmpty()) {
            val resId = context.resources.getIdentifier(url, "drawable", context.packageName)
            if (resId != 0) resId else "https://images.unsplash.com/photo-1558981403-c5f9899a28bc"
        } else {
            "https://images.unsplash.com/photo-1558981403-c5f9899a28bc"
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("UNIT SPECIFICATIONS", fontWeight = FontWeight.Black, letterSpacing = 2.sp, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        AsyncImage(
                            model = userImageUri,
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
            com.example.mad_final.ui.components.TechnicalGridBackground()
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Image
                Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                    val placeholderPainter = rememberVectorPainter(Icons.Default.Refresh)
                    val errorPainter = rememberVectorPainter(Icons.Default.Warning)
                    
                    AsyncImage(
                        model = imageModel,
                        contentDescription = "Image for ${moto.model}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = placeholderPainter,
                        error = errorPainter,
                        onLoading = { android.util.Log.d("MotorcycleDetail", "Loading image: $imageModel") },
                        onError = { android.util.Log.e("MotorcycleDetail", "Error loading image: $imageModel", it.result.throwable) }
                    )
                    
                    // Technical Tag
                    Surface(
                        color = Primary,
                        modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
                    ) {
                        Text(
                            text = "REF: ${moto.id.take(8).uppercase()}",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = moto.brand.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Secondary,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = moto.model,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 46.sp,
                        letterSpacing = (-1).sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Specs Grid
                    Row(modifier = Modifier.fillMaxWidth()) {
                        SpecItem(label = "YEAR", value = moto.year.toString(), modifier = Modifier.weight(1f))
                        SpecItem(label = "TYPE", value = moto.type.uppercase(), modifier = Modifier.weight(1f))
                        SpecItem(label = "STATUS", value = if (moto.availability) "READY" else "IN SERVICE", modifier = Modifier.weight(1f))
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text(
                        "TECHNICAL OVERVIEW",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Primary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = moto.description,
                        fontSize = 15.sp,
                        color = Neutral,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Service History Preview
                    Surface(
                        border = BorderStroke(2.dp, Color.Black),
                        color = Color(0xFFF8F8F8),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("LAST SERVICE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Neutral)
                                Text("COMPLETED - OCT 12, 2023", fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Primary, modifier = Modifier.size(16.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    // Action Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("DAILY RATE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Neutral)
                            Text("$${moto.pricePerDay}", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Primary)
                        }
                        
                        Button(
                            onClick = { onBookClick(moto.id) },
                            modifier = Modifier.weight(2f).height(56.dp),
                            shape = RoundedCornerShape(0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (moto.availability) Secondary else Color.LightGray
                            ),
                            enabled = moto.availability,
                            border = BorderStroke(2.dp, Color.Black)
                        ) {
                            Text(
                                text = if (moto.availability) "INITIATE BOOKING" else "UNIT UNAVAILABLE",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun SpecItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Black, color = Neutral)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = Primary)
    }
}
