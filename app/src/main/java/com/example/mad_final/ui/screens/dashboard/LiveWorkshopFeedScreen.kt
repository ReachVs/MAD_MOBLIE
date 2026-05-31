package com.example.mad_final.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import com.example.mad_final.ui.components.AppDrawerContent
import com.example.mad_final.ui.components.TechnicalGridBackground
import com.example.mad_final.ui.navigation.Screen

import com.example.mad_final.ui.theme.Primary
import com.example.mad_final.ui.theme.Secondary
import com.example.mad_final.ui.theme.Neutral
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveWorkshopFeedScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit = {},
    onDashboardClick: () -> Unit = {},
    onExploreClick: () -> Unit = {},
    onMyBookingsClick: () -> Unit = {},
    onBookClick: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                currentRoute = Screen.LiveFeed.route,
                onNavigate = { route ->
                    scope.launch { drawerState.close() }
                    when (route) {
                        Screen.Dashboard.route -> onDashboardClick()
                        Screen.Catalog.route -> onExploreClick()
                        Screen.MyBookings.route -> onMyBookingsClick()
                        Screen.Booking.createRoute("custom_unit") -> onBookClick()
                        Screen.LiveFeed.route -> {} // Already here
                    }
                },
                onLogout = onLogout,
                onClose = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "LIVE WORKSHOP FEED",
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontSize = 16.sp,
                            color = Primary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Primary)
                        }
                    },
                    actions = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Primary)
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
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
            ) {
                TechnicalGridBackground()
                
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Technical Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Primary)
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "SYSTEM STATUS: ACTIVE",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Surface(
                                color = Secondary,
                                shape = RoundedCornerShape(0.dp)
                            ) {
                                Text(
                                    "LIVE",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                "REAL-TIME\nOPERATIONS",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                lineHeight = 30.sp,
                                letterSpacing = (-1).sp,
                                color = Primary
                            )
                            Text(
                                "MONITORING MULTIPLE SERVICE BAYS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Neutral,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        items(mockFeedItems) { item ->
                            FeedItemCard(item)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        item {
                            TechnicalFooter()
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeedItemCard(item: FeedItem) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val imageModel = androidx.compose.runtime.remember(item.imageUrl) {
        val url = item.imageUrl.trim()
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        border = BorderStroke(2.dp, Color.Black),
        color = Color.White
    ) {
        Column {
            Box {
                val placeholderPainter = rememberVectorPainter(Icons.Default.Refresh)
                val errorPainter = rememberVectorPainter(Icons.Default.Warning)

                AsyncImage(
                    model = imageModel,
                    contentDescription = "Feed image: ${item.title}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .drawBehind {
                            drawLine(
                                color = Color.Black,
                                start = Offset(0f, size.height),
                                end = Offset(size.width, size.height),
                                strokeWidth = 2.dp.toPx()
                            )
                        },
                    contentScale = ContentScale.Crop,
                    placeholder = placeholderPainter,
                    error = errorPainter,
                    onLoading = { android.util.Log.d("LiveFeed", "Loading image: $imageModel") },
                    onError = { android.util.Log.e("LiveFeed", "Error loading image: $imageModel", it.result.throwable) }
                )
                if (item.isLive) {
                    Surface(
                        color = Secondary,
                        modifier = Modifier.padding(12.dp),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Text(
                            "LIVE FEED",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            item.title.uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Primary,
                            letterSpacing = (-0.5).sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            item.description,
                            fontSize = 13.sp,
                            color = Neutral,
                            fontWeight = FontWeight.Black,
                            lineHeight = 18.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                    drawLine(
                        color = Color.Black.copy(alpha = 0.2f),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(if (item.isLive) Secondary else Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            item.timestamp,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Neutral,
                            letterSpacing = 1.sp
                        )
                    }
                    
                    Text(
                        "REF: ${item.title.take(3).uppercase()}-${(100..999).random()}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Neutral.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun TechnicalFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(2.dp)) {
            drawLine(
                color = Color.Black,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 2.dp.toPx()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "END OF CURRENT FEED DATA",
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = Primary,
            letterSpacing = 2.sp
        )
        Text(
            "SYSTEM VERSION 2.0.4-BETA // MAD APE SECURE",
            fontSize = 8.sp,
            color = Neutral,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
    }
}


data class FeedItem(
    val title: String,
    val description: String,
    val timestamp: String,
    val imageUrl: String,
    val isLive: Boolean = false
)

val mockFeedItems = listOf(
    FeedItem(
        "Bay 1: Engine Tuning",
        "BMW S1000RR - Performance mapping in progress.",
        "JUST NOW",
        "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?q=80&w=400&auto=format&fit=crop",
        true
    ),
    FeedItem(
        "Bay 4: Tire Change",
        "Husqvarna Vitpilen - Fitting Pirelli Rosso IV.",
        "5 MINS AGO",
        "https://images.unsplash.com/photo-1580273916550-e323be2ae537?q=80&w=400&auto=format&fit=crop",
        true
    ),
    FeedItem(
        "Bay 2: Annual Service",
        "Ducati Panigale - Oil & Filter change completed.",
        "1 HOUR AGO",
        "https://images.unsplash.com/photo-1599819811279-d5ad9cccf838?q=80&w=400&auto=format&fit=crop",
        false
    )
)
