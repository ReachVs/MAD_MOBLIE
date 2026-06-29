package com.example.mad_final.ui.screens.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mad_final.domain.models.Motorcycle
import com.example.mad_final.domain.repository.MotorcycleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.mad_final.domain.models.WorkshopService

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val repository: MotorcycleRepository,
    private val serviceRepository: com.example.mad_final.domain.repository.ServiceRepository,
    private val bookingRepository: com.example.mad_final.domain.repository.BookingRepository,
    private val authRepository: com.example.mad_final.domain.repository.AuthRepository,
    private val cartRepository: com.example.mad_final.domain.repository.CartRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("ALL")
    val selectedCategory: StateFlow<String> = _selectedCategory

    val userImageUri: StateFlow<String?> = authRepository.getUserImageUri()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userName: StateFlow<String?> = authRepository.getUserName()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val bookings: StateFlow<List<com.example.mad_final.domain.models.Booking>> = authRepository.getUserId()
        .flatMapLatest { userId: String? ->
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

    val motorcycles: StateFlow<List<Motorcycle>> = repository.getMotorcycles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val groupedServices: StateFlow<Map<String, Map<String, List<WorkshopService>>>> = 
        combine(serviceRepository.getServices(), _selectedCategory) { services, category ->
            val filtered = if (category == "ALL") services
            else services.filter { 
                it.category.contains(category, ignoreCase = true) || 
                category.contains(it.category, ignoreCase = true) 
            }
            
            filtered.groupBy { it.category }
                .mapValues { (_, categoryServices) ->
                    categoryServices.groupBy { it.subCategory ?: "GENERAL SERVICES" }
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    val hasActiveService: StateFlow<Boolean> = bookings.map { list ->
        list.any { 
            it.status != com.example.mad_final.domain.models.BookingStatus.CANCELLED && 
            it.status != com.example.mad_final.domain.models.BookingStatus.COMPLETED 
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val selectedServiceIds: StateFlow<Set<String>> = cartRepository.selectedServiceIds

    val selectedServices = combine(serviceRepository.getServices(), selectedServiceIds) { allServices, selectedIds ->
        allServices.filter { selectedIds.contains(it.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalPrice = selectedServices.map { list ->
        list.sumOf { it.price }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalDuration = selectedServices.map { list ->
        list.sumOf { it.duration.replace("mins", "").trim().toIntOrNull() ?: 0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        refresh()
    }

    fun toggleService(serviceId: String) {
        cartRepository.toggleService(serviceId)
    }

    fun clearSelection() {
        cartRepository.clearCart()
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refreshMotorcycles()
        }
    }
}
