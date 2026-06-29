package com.example.mad_final.ui.screens.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mad_final.domain.models.Booking
import com.example.mad_final.domain.repository.BookingRepository
import com.example.mad_final.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MyBookingsViewModel @Inject constructor(
    private val repository: BookingRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val bookings: StateFlow<List<Booking>> = combine(
        authRepository.getUserId(),
        authRepository.getGuestId()
    ) { userId: String?, guestId: String? ->
        userId ?: guestId
    }.flatMapLatest { id: String? ->
        if (id != null) {
            repository.getBookingsByUserId(id)
        } else {
            flowOf(emptyList())
        }
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val userImageUri: StateFlow<String?> = authRepository.getUserImageUri()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userName: StateFlow<String?> = authRepository.getUserName()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
