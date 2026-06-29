package com.example.mad_final.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.tooling.preview.Preview

import com.example.mad_final.ui.components.TechnicalGridBackground
import com.example.mad_final.ui.theme.Primary
import com.example.mad_final.ui.theme.Secondary
import com.example.mad_final.ui.theme.Neutral

@Composable
fun LoginScreen(
    onLoginSuccess: (Boolean) -> Unit, // Boolean for isAdmin
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by viewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        (state as? AuthState.Success)?.let {
            onLoginSuccess(it.isAdmin)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        TechnicalGridBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            
            Text(
                text = "MAD APE",
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-2).sp,
                color = Primary
            )
            Text(
                text = "TECHNICAL ACCESS PORTAL",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = Secondary
            )
            
            Spacer(modifier = Modifier.height(64.dp))

            Text(
                "CREDENTIALS REQUIRED",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Neutral
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            TechnicalTextField(
                value = email,
                onValueChange = { email = it },
                label = "ID / EMAIL",
                icon = Icons.Default.Email
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            TechnicalTextField(
                value = password,
                onValueChange = { password = it },
                label = "ACCESS KEY",
                icon = Icons.Default.Lock,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled = state !is AuthState.Loading,
                border = BorderStroke(2.dp, Color.Black)
            ) {
                if (state is AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("AUTHENTICATE", fontSize = 16.sp, fontWeight = FontWeight.Black)
                }
            }

            if (state is AuthState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = Secondary.copy(alpha = 0.1f),
                    border = BorderStroke(2.dp, Color.Black),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        text = (state as AuthState.Error).message.uppercase(),
                        color = Secondary,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = onNavigateToRegister,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    "INITIALIZE NEW ACCOUNT",
                    color = Primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TechnicalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false
) {
    Column {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Neutral,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(0.dp),
            leadingIcon = { Icon(icon, contentDescription = null, tint = Primary) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Neutral
            ),
            singleLine = true
        )
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreen_Preview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Text(
                text = "MAD APE",
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-2).sp,
                color = Primary
            )
            Text(
                text = "TECHNICAL ACCESS PORTAL",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = Secondary
            )

            Spacer(modifier = Modifier.height(64.dp))

            Text(
                "CREDENTIALS REQUIRED",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Neutral
            )

            Spacer(modifier = Modifier.height(16.dp))

            TechnicalTextField(
                value = "operator@example.com",
                onValueChange = {},
                label = "ID / EMAIL",
                icon = Icons.Default.Email
            )

            Spacer(modifier = Modifier.height(24.dp))

            TechnicalTextField(
                value = "password",
                onValueChange = {},
                label = "ACCESS KEY",
                icon = Icons.Default.Lock,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("AUTHENTICATE", fontSize = 16.sp, fontWeight = FontWeight.Black)
            }

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = { },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    "INITIALIZE NEW ACCOUNT",
                    color = Primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
