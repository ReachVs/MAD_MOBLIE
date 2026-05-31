package com.example.mad_final.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mad_final.domain.models.Booking
import com.example.mad_final.domain.models.Motorcycle
import com.example.mad_final.domain.repository.BookingRepository
import com.example.mad_final.domain.repository.MotorcycleRepository
import com.example.mad_final.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val motorcycleRepository: MotorcycleRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val bookings: StateFlow<List<Booking>> = authRepository.getUserEmail()
        .flatMapLatest { email ->
            if (email != null) {
                bookingRepository.getBookingsByUserId(email)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val motorcycles: StateFlow<List<Motorcycle>> = bookings.flatMapLatest { userBookings ->
        val bookedMotorcycleIds = userBookings.map { it.motorcycleId }.distinct()
        motorcycleRepository.getMotorcycles().map { allMotorcycles: List<Motorcycle> ->
            allMotorcycles.filter { it.id in bookedMotorcycleIds }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}
