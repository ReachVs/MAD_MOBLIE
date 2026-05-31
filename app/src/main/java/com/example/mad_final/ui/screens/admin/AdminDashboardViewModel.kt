package com.example.mad_final.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mad_final.domain.models.Booking
import com.example.mad_final.domain.models.BookingStatus
import com.example.mad_final.domain.repository.BookingRepository
import com.example.mad_final.domain.models.Priority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val serviceRepository: com.example.mad_final.domain.repository.ServiceRepository
) : ViewModel() {

    val bookings: StateFlow<List<Booking>> = bookingRepository.getBookings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val dailyRevenue: StateFlow<Double> = bookingRepository.getBookings()
        .map { list ->
            val today = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.timeInMillis
            list.filter { it.startDate >= today }.sumOf { it.totalPrice }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val lifetimeRevenue: StateFlow<Double> = bookingRepository.getBookings()
        .map { list -> list.sumOf { it.totalPrice } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val totalCustomers: StateFlow<Int> = bookingRepository.getBookings()
        .map { list -> list.map { it.userId }.distinct().size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val activeOrdersCount: StateFlow<Int> = bookingRepository.getBookings()
        .map { list -> list.count { it.status != BookingStatus.CANCELLED && it.status != BookingStatus.COMPLETED } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val urgentTasksCount: StateFlow<Int> = bookingRepository.getBookings()
        .map { list -> list.count { it.priority == Priority.URGENT || it.priority == Priority.HIGH } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val dailyRevenueGrowth: StateFlow<Double> = bookingRepository.getBookings()
        .map { list ->
            val calendar = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            val todayStart = calendar.timeInMillis
            val yesterdayStart = todayStart - 86400000
            
            val todayRevenue = list.filter { it.startDate >= todayStart }.sumOf { it.totalPrice }
            val yesterdayRevenue = list.filter { it.startDate in yesterdayStart until todayStart }.sumOf { it.totalPrice }
            
            if (yesterdayRevenue == 0.0) {
                if (todayRevenue > 0) 100.0 else 0.0
            } else {
                ((todayRevenue - yesterdayRevenue) / yesterdayRevenue) * 100.0
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val services: StateFlow<List<com.example.mad_final.domain.models.WorkshopService>> = serviceRepository.getServices()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateServicePrice(serviceId: String, newPrice: String) {
        viewModelScope.launch {
            val service = serviceRepository.getServiceById(serviceId)
            service?.let {
                // Ensure the price has a $ prefix if it's a numeric value
                val formattedPrice = if (newPrice.isNotEmpty() && !newPrice.startsWith("$")) {
                    if (newPrice.all { it.isDigit() || it == '.' }) "$$newPrice" else newPrice
                } else {
                    newPrice
                }
                serviceRepository.updateService(it.copy(price = formattedPrice))
            }
        }
    }

    val revenueData: StateFlow<List<Double>> = bookingRepository.getBookings()
        .map { list ->
            // Last 7 days revenue for sparkline
            val calendar = java.util.Calendar.getInstance()
            (0..6).map { dayOffset ->
                calendar.timeInMillis = System.currentTimeMillis()
                calendar.add(java.util.Calendar.DAY_OF_YEAR, -dayOffset)
                val startOfDay = calendar.apply {
                    set(java.util.Calendar.HOUR_OF_DAY, 0)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }.timeInMillis
                val endOfDay = startOfDay + 86400000
                list.filter { it.startDate in startOfDay until endOfDay }.sumOf { it.totalPrice }
            }.reversed()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createWorkOrder(motorcycleId: String, userId: String, description: String) {
        viewModelScope.launch {
            val booking = Booking(
                id = java.util.UUID.randomUUID().toString(),
                motorcycleId = motorcycleId,
                userId = userId,
                startDate = System.currentTimeMillis(),
                endDate = System.currentTimeMillis() + 86400000,
                totalPrice = 150.0,
                status = BookingStatus.PENDING,
                paymentStatus = com.example.mad_final.domain.models.PaymentStatus.UNPAID,
                workDescription = description,
                technicianName = "Unassigned",
                priority = Priority.NORMAL
            )
            bookingRepository.createBooking(booking)
        }
    }
}
