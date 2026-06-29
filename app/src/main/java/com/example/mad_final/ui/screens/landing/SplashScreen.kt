package com.example.mad_final.ui.screens.landing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mad_final.ui.screens.auth.AuthViewModel
import com.example.mad_final.ui.screens.auth.SessionState

@Composable
fun SplashScreen(
    onNavigateToAdmin: () -> Unit,
    onNavigateToCustomer: () -> Unit,
    onNavigateToLanding: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val sessionState by viewModel.sessionState.collectAsStateWithLifecycle()

    LaunchedEffect(sessionState) {
        when (sessionState) {
            is SessionState.Authenticated -> {
                val role = (sessionState as SessionState.Authenticated).role
                if (role == "ADMIN") {
                    onNavigateToAdmin()
                } else {
                    onNavigateToCustomer()
                }
            }
            is SessionState.Unauthenticated -> {
                onNavigateToLanding()
            }
            else -> { /* Still loading */ }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        com.example.mad_final.ui.components.TechnicalGridBackground()
        CircularProgressIndicator(
            color = Color.Black,
            strokeWidth = 4.dp
        )
    }
}
