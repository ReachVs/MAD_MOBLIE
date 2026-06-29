package com.example.mad_final.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mad_final.data.remote.dto.auth.LoginRequest
import com.example.mad_final.data.remote.dto.auth.RegisterRequest
import com.example.mad_final.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    val sessionState: StateFlow<SessionState> = combine(
        repository.isUserLoggedIn(),
        repository.getUserRole()
    ) { isLoggedIn, role ->
        if (!isLoggedIn) {
            SessionState.Unauthenticated
        } else {
            // If logged in, we must have a role. Fallback to CUSTOMER if null.
            SessionState.Authenticated(role ?: "CUSTOMER")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SessionState.Loading
    )

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.login(LoginRequest(email, password))
                .onSuccess { response ->
                    val isAdmin = response.role == "ADMIN"
                    _authState.value = AuthState.Success(isAdmin)
                }
                .onFailure { _authState.value = AuthState.Error(it.message ?: "Login failed") }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.register(RegisterRequest(name, email, password))
                .onSuccess { response ->
                    val isAdmin = response.role == "ADMIN"
                    _authState.value = AuthState.Success(isAdmin)
                }
                .onFailure { _authState.value = AuthState.Error(it.message ?: "Registration failed") }
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val isAdmin: Boolean) : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class SessionState {
    object Loading : SessionState()
    object Unauthenticated : SessionState()
    data class Authenticated(val role: String) : SessionState()
}
