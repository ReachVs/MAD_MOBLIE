package com.example.mad_final.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mad_final.domain.models.Booking
import com.example.mad_final.domain.models.Motorcycle
import com.example.mad_final.domain.repository.BookingRepository
import com.example.mad_final.domain.repository.AuthRepository
import com.example.mad_final.domain.repository.MotorcycleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val userName: StateFlow<String?> = authRepository.getUserName()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userImageUri: StateFlow<String?> = authRepository.getUserImageUri()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userRole: StateFlow<String?> = authRepository.getUserRole()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)


    val bookings: StateFlow<List<Booking>> = authRepository.getUserId()
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
}
