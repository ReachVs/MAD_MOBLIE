package com.example.mad_final.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mad_final.domain.repository.AuthRepository
import com.example.mad_final.domain.repository.BookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val bookings: StateFlow<List<com.example.mad_final.domain.models.Booking>> = authRepository.getUserId()
        .flatMapLatest { userId ->
            if (userId != null) {
                bookingRepository.getBookingsByUserId(userId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val userEmail: StateFlow<String?> = authRepository.getUserEmail()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userName: StateFlow<String?> = authRepository.getUserName()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userImageUri: StateFlow<String?> = authRepository.getUserImageUri()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val adminImageUri: StateFlow<String?> = authRepository.getAdminImageUri()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userRole: StateFlow<String?> = authRepository.getUserRole()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _eventFlow = MutableSharedFlow<ProfileEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun updateProfile(name: String, email: String, imageUri: String?, isAdmin: Boolean = false) {
        viewModelScope.launch {
            authRepository.updateProfile(name, email, imageUri, isAdmin)
            _eventFlow.emit(ProfileEvent.ProfileUpdated)
        }
    }

    fun updatePassword(password: String) {
        viewModelScope.launch {
            val result = authRepository.updatePassword(password)
            if (result.isSuccess) {
                _eventFlow.emit(ProfileEvent.PasswordUpdated)
            } else {
                _eventFlow.emit(ProfileEvent.Error(result.exceptionOrNull()?.message ?: "Unknown error"))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _eventFlow.emit(ProfileEvent.LoggedOut)
        }
    }

    sealed class ProfileEvent {
        object ProfileUpdated : ProfileEvent()
        object PasswordUpdated : ProfileEvent()
        object LoggedOut : ProfileEvent()
        data class Error(val message: String) : ProfileEvent()
    }
}
