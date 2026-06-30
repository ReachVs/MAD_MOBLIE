package com.example.mad_final.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mad_final.domain.models.Booking
import com.example.mad_final.domain.models.BookingStatus
import com.example.mad_final.domain.repository.BookingRepository
import com.example.mad_final.domain.models.Priority
import com.example.mad_final.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val serviceRepository: com.example.mad_final.domain.repository.ServiceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val userName: StateFlow<String?> = authRepository.getUserName()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val adminImageUri: StateFlow<String?> = authRepository.getAdminImageUri()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val bookings: StateFlow<List<Booking>> = bookingRepository.getBookings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val dailyRevenue: StateFlow<Double> = bookingRepository.getBookings()
        .map { list ->
            val calendar = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }
            val todayStart = calendar.timeInMillis
            val todayEnd = todayStart + 86400000
            list.filter { it.startDate in todayStart until todayEnd && it.status != BookingStatus.CANCELLED }.sumOf { it.totalPrice }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val lifetimeRevenue: StateFlow<Double> = bookingRepository.getBookings()
        .map { list -> list.filter { it.status != BookingStatus.CANCELLED }.sumOf { it.totalPrice } }
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
            
            val todayRevenue = list.filter { it.startDate >= todayStart && it.status != BookingStatus.CANCELLED }.sumOf { it.totalPrice }
            val yesterdayRevenue = list.filter { it.startDate in yesterdayStart until todayStart && it.status != BookingStatus.CANCELLED }.sumOf { it.totalPrice }
            
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
                list.filter { it.startDate in startOfDay until endOfDay && it.status != BookingStatus.CANCELLED }.sumOf { it.totalPrice }
            }.reversed()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val revenueByCategory: StateFlow<Map<String, Double>> = combine(
        bookingRepository.getBookings(),
        serviceRepository.getServices()
    ) { bookingList, serviceList ->
        val validBookings = bookingList.filter { it.status != BookingStatus.CANCELLED }
        val categoryTotals = mutableMapOf<String, Double>()
        
        validBookings.forEach { booking ->
            val titles = booking.workDescription.split(", ").map { it.trim() }
            val matchedServices = serviceList.filter { titles.contains(it.title) }
            
            if (matchedServices.isNotEmpty()) {
                val pricePerService = booking.totalPrice / matchedServices.size
                matchedServices.forEach { service ->
                    val current = categoryTotals.getOrDefault(service.category.uppercase(), 0.0)
                    categoryTotals[service.category.uppercase()] = current + pricePerService
                }
            } else {
                val current = categoryTotals.getOrDefault("UNSPECIFIED", 0.0)
                categoryTotals["UNSPECIFIED"] = current + booking.totalPrice
            }
        }
        categoryTotals
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )
}
