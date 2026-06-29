package com.example.mad_final.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mad_final.ui.theme.Neutral
import com.example.mad_final.ui.theme.Secondary

@Composable
fun MadApeBottomNavigation(
    currentRoute: String,
    hasActiveService: Boolean = false,
    onHomeClick: () -> Unit,
    onCatalogClick: () -> Unit,
    onTrackingClick: () -> Unit,
    onBookClick: () -> Unit
) {
    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 2.dp.toPx()
                )
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp, top = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                label = "HOME",
                icon = Icons.Default.Home,
                selected = currentRoute == "home",
                onClick = onHomeClick
            )

            NavItem(
                label = "CATALOG SERVICE",
                icon = Icons.AutoMirrored.Filled.List,
                selected = currentRoute == "catalog",
                onClick = onCatalogClick
            )

            NavItem(
                label = "SERVICE QUEUE",
                icon = if (hasActiveService) Icons.Default.Settings else Icons.Default.Build,
                selected = currentRoute == "tracking",
                onClick = onTrackingClick
            )

            NavItem(
                label = "BOOK SERVICE",
                icon = Icons.Default.AddCircle,
                selected = currentRoute == "booking",
                onClick = onBookClick
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) Secondary else Neutral,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = if (selected) Secondary else Neutral
        )
    }
}
