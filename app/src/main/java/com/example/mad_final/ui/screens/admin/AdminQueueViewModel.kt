package com.example.mad_final.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mad_final.domain.models.Booking
import com.example.mad_final.domain.models.BookingStatus
import com.example.mad_final.domain.repository.BookingRepository
import com.example.mad_final.ui.util.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminQueueViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val partRepository: com.example.mad_final.domain.repository.PartRepository,
    private val authRepository: com.example.mad_final.domain.repository.AuthRepository,
    private val motorcycleRepository: com.example.mad_final.domain.repository.MotorcycleRepository,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    val userName: StateFlow<String?> = authRepository.getUserName()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val adminImageUri: StateFlow<String?> = authRepository.getAdminImageUri()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val motorcycles: StateFlow<List<com.example.mad_final.domain.models.Motorcycle>> = motorcycleRepository.getMotorcycles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedTab = kotlinx.coroutines.flow.MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab

    val allBookings: StateFlow<List<Booking>> = bookingRepository.getBookings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredBookings: StateFlow<List<Booking>> = combine(
        allBookings,
        _selectedTab
    ) { bookings, tab ->
        when (tab) {
            0 -> bookings.filter { 
                it.status != BookingStatus.COMPLETED && 
                it.status != BookingStatus.CANCELLED
            }
            1 -> bookings.filter { 
                it.status != BookingStatus.COMPLETED && 
                it.status != BookingStatus.CANCELLED
            }
            2 -> bookings.filter { 
                it.status == BookingStatus.COMPLETED || 
                it.status == BookingStatus.CANCELLED
            }
            else -> bookings
        }.sortedWith(
            compareByDescending<Booking> { it.priority == com.example.mad_final.domain.models.Priority.URGENT }
                .thenByDescending { it.priority == com.example.mad_final.domain.models.Priority.HIGH }
                .thenByDescending { it.startDate }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }

    val availableParts: StateFlow<List<com.example.mad_final.domain.models.Part>> = partRepository.getAllParts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateBookingStatus(booking: Booking) {
        viewModelScope.launch {
            val oldBooking = allBookings.value.find { it.id == booking.id }
            
            // If status changed to COMPLETED, decrement stock for used parts
            if (oldBooking?.status != BookingStatus.COMPLETED && booking.status == BookingStatus.COMPLETED) {
                var allPartsAvailable = true
                for (partId in booking.usedPartIds) {
                    val part = partRepository.getPartById(partId)
                    if (part == null || part.stockQuantity <= 0) {
                        allPartsAvailable = false
                        break
                    }
                }

                if (allPartsAvailable) {
                    booking.usedPartIds.forEach { partId ->
                        val part = partRepository.getPartById(partId)
                        if (part != null) {
                            partRepository.updateStock(partId, part.stockQuantity - 1)
                        }
                    }
                } else {
                    // Notify about stock issue
                    notificationHelper.showBookingNotification(
                        "STOCK ERROR",
                        "Cannot complete work order #${booking.id.take(4).uppercase()}. One or more parts are out of stock."
                    )
                    return@launch
                }
            }

            bookingRepository.updateBooking(booking)
            
            // Check for status change
            if (oldBooking?.status != booking.status) {
                val statusTitle = when(booking.status) {
                    BookingStatus.REPAIR -> "MAINTENANCE STARTED"
                    BookingStatus.READY_TO_PICK_UP -> "VEHICLE READY"
                    BookingStatus.WAITING_PART -> "AWAITING PARTS"
                    BookingStatus.COMPLETED -> "SERVICE COMPLETED"
                    else -> "STATUS UPDATED"
                }
                
                val statusMessage = when(booking.status) {
                    BookingStatus.REPAIR -> "Our engineers have begun working on your machine."
                    BookingStatus.READY_TO_PICK_UP -> "Your vehicle has passed inspection and is ready for collection."
                    BookingStatus.WAITING_PART -> "We've encountered a delay. Awaiting necessary components."
                    BookingStatus.COMPLETED -> "Transaction complete. Safe riding."
                    else -> "Work order #${booking.id.take(4).uppercase()} status: ${booking.status.name}"
                }

                notificationHelper.showBookingNotification(
                    statusTitle,
                    statusMessage
                )
            }
            
            // Check for priority escalation
            if (oldBooking != null && oldBooking.priority != booking.priority && 
                (booking.priority == com.example.mad_final.domain.models.Priority.URGENT || 
                 booking.priority == com.example.mad_final.domain.models.Priority.HIGH)) {
                notificationHelper.showBookingNotification(
                    "PRIORITY ESCALATION",
                    "Work order #${booking.id.take(4).uppercase()} has been flagged as ${booking.priority.name}."
                )
            }
        }
    }

    fun deleteBooking(bookingId: String) {
        viewModelScope.launch {
            bookingRepository.cancelBooking(bookingId)
        }
    }
}
