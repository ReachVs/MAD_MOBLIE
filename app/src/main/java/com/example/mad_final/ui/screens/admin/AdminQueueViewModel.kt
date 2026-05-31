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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminQueueViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    val bookings: StateFlow<List<Booking>> = bookingRepository.getBookings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateBookingStatus(booking: Booking) {
        viewModelScope.launch {
            val oldBooking = bookings.value.find { it.id == booking.id }
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
