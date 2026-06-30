package com.example.mad_final.ui.screens.profile

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.mad_final.ui.components.AppDrawerContent
import com.example.mad_final.ui.components.TechnicalGridBackground
import com.example.mad_final.ui.screens.admin.AdminDrawerContent
import com.example.mad_final.ui.screens.admin.AdminBottomNavigation
import com.example.mad_final.ui.screens.admin.AdminTopBar
import com.example.mad_final.ui.navigation.Screen
import com.example.mad_final.ui.theme.Primary
import com.example.mad_final.ui.theme.Secondary
import com.example.mad_final.ui.theme.Neutral
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onHomeClick: () -> Unit,
    onCatalogClick: () -> Unit,
    onTrackingClick: () -> Unit,
    onBookClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }

    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()
    val userImageUri by viewModel.userImageUri.collectAsStateWithLifecycle()
    val adminImageUri by viewModel.adminImageUri.collectAsStateWithLifecycle()
    val userRole by viewModel.userRole.collectAsStateWithLifecycle()
    val bookings by viewModel.bookings.collectAsStateWithLifecycle()
    val isAdmin = userRole == "ADMIN"

    var name by remember(userName) { mutableStateOf(userName ?: "") }
    var email by remember(userEmail) { mutableStateOf(userEmail ?: "") }
    var selectedImageUri by remember(userImageUri, adminImageUri, isAdmin) { 
        val currentUri = if (isAdmin) adminImageUri else userImageUri
        mutableStateOf<Uri?>(currentUri?.let { Uri.parse(it) }) 
    }
    
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ProfileViewModel.ProfileEvent.ProfileUpdated -> {
                    snackbarHostState.showSnackbar("Profile updated successfully")
                }
                is ProfileViewModel.ProfileEvent.PasswordUpdated -> {
                    snackbarHostState.showSnackbar("Password updated successfully")
                }
                is ProfileViewModel.ProfileEvent.LoggedOut -> {
                    onLogout()
                }
                is ProfileViewModel.ProfileEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                try {
                    val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    context.contentResolver.takePersistableUriPermission(uri, flag)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                selectedImageUri = uri
            }
        }
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            if (isAdmin) {
                AdminDrawerContent(
                    onDashboardClick = {
                        scope.launch { drawerState.close() }
                        onHomeClick()
                    },
                    onInventoryClick = {
                        scope.launch { drawerState.close() }
                        onCatalogClick()
                    },
                    onQueueClick = {
                        scope.launch { drawerState.close() }
                        onTrackingClick()
                    },
                    onRevenueClick = { scope.launch { drawerState.close() } },
                    onCalendarClick = { scope.launch { drawerState.close() } },
                    onProfileClick = { scope.launch { drawerState.close() } },
                    onLogout = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    },
                    onClose = { scope.launch { drawerState.close() } },
                    currentRoute = Screen.Profile.route,
                    userName = userName,
                    userImageUri = adminImageUri
                )
            } else {
                AppDrawerContent(
                    currentRoute = Screen.Profile.route,
                    userName = userName,
                    userImageUri = userImageUri,
                    isAdmin = false,
                    onNavigate = { route ->
                        scope.launch { drawerState.close() }
                        when (route) {
                            Screen.Dashboard.route -> onHomeClick()
                            Screen.Catalog.route -> onCatalogClick()
                            Screen.MyBookings.route -> onTrackingClick()
                            Screen.Booking.createRoute("custom_unit") -> onBookClick()
                            Screen.Profile.route -> {}
                        }
                    },
                    onLogout = onLogout,
                    onClose = { scope.launch { drawerState.close() } }
                )
            }
        }
    )
{
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                if (isAdmin) {
                    AdminTopBar(
                        title = "ADMIN PROFILE",
                        userImageUri = adminImageUri,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onProfileClick = {}, // Already on profile
                        onCalendarClick = null
                    )
                } else {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                "MAD APE",
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                fontSize = 20.sp
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = { showLogoutDialog = true }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = "Logout",
                                    tint = Secondary
                                )
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
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Square
                            )
                        }
                    )
                }
            },
            bottomBar = {
                if (isAdmin) {
                    AdminBottomNavigation(
                        onDashboardClick = onHomeClick,
                        onInventoryClick = onCatalogClick,
                        onQueueClick = onTrackingClick,
                        currentRoute = Screen.Profile.route
                    )
                } else {
                    val hasActiveService = bookings.any { 
                        it.status != com.example.mad_final.domain.models.BookingStatus.CANCELLED && 
                        it.status != com.example.mad_final.domain.models.BookingStatus.COMPLETED 
                    }
                    com.example.mad_final.ui.components.MadApeBottomNavigation(
                        currentRoute = "profile",
                        hasActiveService = hasActiveService,
                        onHomeClick = onHomeClick,
                        onCatalogClick = onCatalogClick,
                        onTrackingClick = onTrackingClick,
                        onBookClick = onBookClick
                    )
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                if (isAdmin) {
                    TechnicalGridBackground()
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Picture Section
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                width = 4.dp,
                                color = if (isAdmin) Primary else Secondary,
                                shape = CircleShape
                            )
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(LocalContext.current)
                                        .data(data = selectedImageUri)
                                        .crossfade(true)
                                        .build()
                                ),
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            val displayUri = if (isAdmin) adminImageUri else userImageUri
                            if (displayUri != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        ImageRequest.Builder(LocalContext.current)
                                            .data(data = displayUri)
                                            .crossfade(true)
                                            .build()
                                    ),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(32.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            tonalElevation = 4.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change Photo",
                                modifier = Modifier.padding(6.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    if (isAdmin) {
                        Text(
                            "ADMINISTRATOR ACCESS",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = Primary,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Information Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = if (isAdmin) RoundedCornerShape(0.dp) else MaterialTheme.shapes.medium,
                        border = if (isAdmin) BorderStroke(2.dp, Color.Black) else null
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Personal Information",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Display Name") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.Person, null) },
                                singleLine = true,
                                shape = if (isAdmin) RoundedCornerShape(0.dp) else MaterialTheme.shapes.medium
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email Address") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.Email, null) },
                                singleLine = true,
                                shape = if (isAdmin) RoundedCornerShape(0.dp) else MaterialTheme.shapes.medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Actions Section
                    Button(
                        onClick = {
                            viewModel.updateProfile(
                                name,
                                email,
                                selectedImageUri?.toString(),
                                isAdmin = isAdmin
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = if (isAdmin) RoundedCornerShape(0.dp) else MaterialTheme.shapes.medium,
                        colors = if (isAdmin) ButtonDefaults.buttonColors(containerColor = Primary) else ButtonDefaults.buttonColors()
                    ) {
                        Icon(Icons.Default.Save, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Save Changes", fontWeight = if (isAdmin) FontWeight.Black else FontWeight.Normal)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { showPasswordDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = if (isAdmin) RoundedCornerShape(0.dp) else MaterialTheme.shapes.medium,
                        border = if (isAdmin) BorderStroke(2.dp, Color.Black) else ButtonDefaults.outlinedButtonBorder
                    ) {
                        Icon(Icons.Default.Lock, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Change Password", fontWeight = if (isAdmin) FontWeight.Black else FontWeight.Normal, color = if (isAdmin) Color.Black else MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { newPassword ->
                viewModel.updatePassword(newPassword)
                showPasswordDialog = false
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.border(2.dp, Color.Black),
            containerColor = MaterialTheme.colorScheme.surface,
            title = { 
                Text(
                    "LOGOUT", 
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.headlineSmall
                ) 
            },
            text = { 
                Text(
                    "ARE YOU SURE YOU WANT TO SIGN OUT?",
                    fontWeight = FontWeight.Medium
                ) 
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                    },
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Secondary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.border(2.dp, Color.Black)
                ) {
                    Text("LOGOUT", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false },
                    shape = RoundedCornerShape(0.dp),
                    border = BorderStroke(2.dp, Color.Black),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black
                    )
                ) {
                    Text("CANCEL", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("New Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm New Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    isError = confirmPassword.isNotEmpty() && password != confirmPassword
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(password) },
                enabled = password.isNotEmpty() && password == confirmPassword
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

