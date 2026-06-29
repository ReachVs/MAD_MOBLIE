package com.example.mad_final.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mad_final.R
import com.example.mad_final.ui.theme.Primary
import com.example.mad_final.ui.theme.Secondary
import com.example.mad_final.ui.theme.Neutral

@Composable
fun HeroSection(onActionClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .drawBehind {
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2.dp.toPx()
                )
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.washing),
            contentDescription = "Hero Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "PRECISION\nENGINEERED",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                lineHeight = 46.sp,
                letterSpacing = (-1).sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "Custom builds and technical service for the modern enthusiast.",
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                modifier = Modifier.width(280.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(2.dp, Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    "BOOK SERVICE",
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun BrandStatsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(2.dp, Color.Black),
            color = Color.White,
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Λ", color = Secondary, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(48.dp))
                Text("0.01mm", fontSize = 32.sp, fontWeight = FontWeight.Black)
                Text("TOLERANCE THRESHOLD", fontSize = 10.sp, color = Neutral, fontWeight = FontWeight.Black)
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(2.dp, Color.Black),
            color = Color.White,
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("CLINICAL", fontWeight = FontWeight.Black, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Our workshop operates with the sterile discipline of an operating theater. Every bolt is torqued to exact manufacturer specifications.",
                    fontSize = 14.sp,
                    color = Neutral,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.Black, thickness = 2.dp)
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Primary,
            border = BorderStroke(2.dp, Color.Black),
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Icon(Icons.Default.Build, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("OPS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Text("FULL RESTORATION & PERFORMANCE TUNING", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun FeaturedServicesSection(onCatalogClick: () -> Unit) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            "OUR SPECIALTIES",
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp,
            color = Neutral
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            ServiceFeatureCard(
                modifier = Modifier.weight(1f),
                title = "Motorcycle Dyno Tuning",
                description = "Unlock your motorcycle's full potential with our expert dyno tuning service!",
                icon = Icons.Default.Settings,
                tag = "MOST POPULAR",
                onAction = onCatalogClick
            )
            Spacer(modifier = Modifier.width(12.dp))
            ServiceFeatureCard(
                modifier = Modifier.weight(1f),
                title = "Motorcycle Maintenance",
                description = "Keep your ride running strong with our professional maintenance service!",
                icon = Icons.Default.Build,
                onAction = onCatalogClick
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            ServiceFeatureCard(
                modifier = Modifier.weight(1f),
                title = "Motorcycle Wash",
                description = "Give your bike the shine it deserves with our premium wash service!",
                icon = Icons.Default.Face,
                onAction = onCatalogClick
            )
            Spacer(modifier = Modifier.width(12.dp))
            ServiceFeatureCard(
                modifier = Modifier.weight(1f),
                title = "Aftermarket Parts",
                description = "Boost your bike's performance and style with our wide range of premium parts!",
                icon = Icons.Default.AddCircle,
                onAction = onCatalogClick
            )
        }
    }
}

@Composable
fun ServiceFeatureCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: ImageVector,
    tag: String? = null,
    onAction: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        border = BorderStroke(2.dp, Color.Black),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(
                color = Secondary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = Secondary, modifier = Modifier.size(24.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                title.uppercase(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 18.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                description,
                fontSize = 10.sp,
                color = Neutral,
                lineHeight = 14.sp,
                maxLines = 3,
                fontWeight = FontWeight.Bold
            )
            
            if (tag != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Secondary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(0.dp),
                    border = BorderStroke(1.dp, Secondary)
                ) {
                    Text(
                        tag,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = Secondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = Secondary),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.height(32.dp).fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
            ) {
                Text("EXPLORE", fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun ContactSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "CONTACT",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.width(40.dp).height(2.dp).background(Secondary))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "OUR SOCIAL MEDIA PLATFORMS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = Neutral
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SocialIcon(Icons.AutoMirrored.Filled.Send, "Telegram", Color(0xFF229ED9))
            SocialIcon(Icons.Default.ThumbUp, "Facebook", Color(0xFF1877F2))
            SocialIcon(Icons.Default.Favorite, "Instagram", Color(0xFFE4405F))
        }
    }
}

@Composable
fun SocialIcon(icon: ImageVector, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(0.dp),
            color = Color.White,
            border = BorderStroke(2.dp, Color.Black)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun BrandFooterSection(showLinks: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "MAD APE",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            letterSpacing = 2.sp
        )
        
        if (showLinks) {
            Spacer(modifier = Modifier.height(32.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FooterLink("PRIVACY")
                FooterLink("TERMS")
                FooterLink("SUPPORT")
            }
            Spacer(modifier = Modifier.height(8.dp))
            FooterLink("CONTACT")
            Spacer(modifier = Modifier.height(48.dp))
        } else {
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        Text(
            "© 2024 MAD APE MOTORWORKS. PRECISION ENGINEERED.",
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 8.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun FooterLink(text: String) {
    Text(text, color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Black)
}
