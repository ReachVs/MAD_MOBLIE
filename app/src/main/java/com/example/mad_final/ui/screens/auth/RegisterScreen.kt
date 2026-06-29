package com.example.mad_final.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.mad_final.ui.components.TechnicalGridBackground
import com.example.mad_final.ui.theme.Primary
import com.example.mad_final.ui.theme.Secondary
import com.example.mad_final.ui.theme.Neutral

@Composable
fun RegisterScreen(
    onRegisterSuccess: (Boolean) -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by viewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        (state as? AuthState.Success)?.let {
            onRegisterSuccess(it.isAdmin)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        TechnicalGridBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "REGISTRATION",
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp,
                color = Primary
            )
            Text(
                text = "NEW USER INITIALIZATION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = Secondary
            )
            
            Spacer(modifier = Modifier.height(48.dp))

            TechnicalTextField(
                value = name,
                onValueChange = { name = it },
                label = "OPERATOR NAME",
                icon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(24.dp))

            TechnicalTextField(
                value = email,
                onValueChange = { email = it },
                label = "COMMUNICATION / EMAIL",
                icon = Icons.Default.Email
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            TechnicalTextField(
                value = password,
                onValueChange = { password = it },
                label = "SECURE ACCESS KEY",
                icon = Icons.Default.Lock,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { viewModel.register(name, email, password) },
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
                    Text("CREATE ACCOUNT", fontSize = 16.sp, fontWeight = FontWeight.Black)
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
                onClick = onNavigateToLogin,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    "BACK TO AUTHENTICATION",
                    color = Primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun RegisterScreen_Preview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "REGISTRATION",
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp,
                color = Primary
            )
            Text(
                text = "NEW USER INITIALIZATION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = Secondary
            )

            Spacer(modifier = Modifier.height(48.dp))

            TechnicalTextField(
                value = "Operator Name",
                onValueChange = {},
                label = "OPERATOR NAME",
                icon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(24.dp))

            TechnicalTextField(
                value = "operator@example.com",
                onValueChange = {},
                label = "COMMUNICATION / EMAIL",
                icon = Icons.Default.Email
            )

            Spacer(modifier = Modifier.height(24.dp))

            TechnicalTextField(
                value = "password",
                onValueChange = {},
                label = "SECURE ACCESS KEY",
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
                Text("CREATE ACCOUNT", fontSize = 16.sp, fontWeight = FontWeight.Black)
            }

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = { },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    "BACK TO AUTHENTICATION",
                    color = Primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
