package com.example.mad_final.ui.screens.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mad_final.domain.models.Booking
import com.example.mad_final.domain.models.BookingStatus
import com.example.mad_final.domain.models.Motorcycle
import com.example.mad_final.domain.models.PaymentStatus
import com.example.mad_final.domain.repository.AuthRepository
import com.example.mad_final.domain.repository.BookingRepository
import com.example.mad_final.domain.repository.MotorcycleRepository
import com.example.mad_final.ui.util.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class BookingUiState(
    val step: Int = 1,
    val manufacturer: String = "",
    val model: String = "",
    val year: String = "",
    val serviceIntent: String = "",
    val configuration: String = "",
    val totalPrice: Double = 0.0,
    val selectedDate: Long? = System.currentTimeMillis(),
    val selectedTime: String = "09:00 AM",
    val services: List<com.example.mad_final.domain.models.WorkshopService> = emptyList(),
    val selectedServiceIds: Set<String> = emptySet(),
    val userImageUri: String? = null,
    val isFromCatalog: Boolean = false,
    val isMotorcycleValid: Boolean = false,
    val isServiceValid: Boolean = false
)

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val motorcycleRepository: MotorcycleRepository,
    private val bookingRepository: BookingRepository,
    private val serviceRepository: com.example.mad_final.domain.repository.ServiceRepository,
    private val authRepository: AuthRepository,
    private val cartRepository: com.example.mad_final.domain.repository.CartRepository,
    private val notificationHelper: NotificationHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val config: String? = savedStateHandle["config"]
    private val motorcycleId: String? = savedStateHandle["motorcycleId"]

    private val _uiState = MutableStateFlow(BookingUiState(isFromCatalog = !config.isNullOrEmpty()))
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    init {
        initializeData()
        observeExternalStates()
    }

    private fun initializeData() {
        config?.let {
            if (it.isNotEmpty()) {
                val ids = it.split(",").toSet()
                cartRepository.setSelectedServices(ids)
                _uiState.update { state -> state.copy(configuration = it) }
            }
        }
        
        viewModelScope.launch {
            serviceRepository.getServices().collect { allServices ->
                _uiState.update { it.copy(services = allServices) }
                if (cartRepository.selectedServiceIds.value.isNotEmpty()) {
                    updateIntentFromSelection(allServices, cartRepository.selectedServiceIds.value)
                } else {
                    loadInitialMachineData(allServices)
                }
            }
        }
    }

    private fun observeExternalStates() {
        viewModelScope.launch {
            combine(
                cartRepository.selectedServiceIds,
                authRepository.getUserImageUri(),
                _uiState.map { it.manufacturer }.distinctUntilChanged(),
                _uiState.map { it.model }.distinctUntilChanged(),
                _uiState.map { it.year }.distinctUntilChanged()
            ) { selectedIds, imageUri, brand, model, year ->
                val isValidMachine = brand.isNotBlank() && model.isNotBlank() && year.isNotBlank()
                val isValidService = selectedIds.isNotEmpty()
                val total = calculateTotal(selectedIds)
                
                _uiState.update { it.copy(
                    selectedServiceIds = selectedIds,
                    userImageUri = imageUri,
                    isMotorcycleValid = isValidMachine,
                    isServiceValid = isValidService,
                    totalPrice = total
                ) }
            }.collectLatest { }
        }
    }

    private fun calculateTotal(selectedIds: Set<String>): Double {
        return _uiState.value.services
            .filter { selectedIds.contains(it.id) }
            .sumOf { it.price }
    }

    private suspend fun loadInitialMachineData(allServices: List<com.example.mad_final.domain.models.WorkshopService>) {
        motorcycleId?.let { id ->
            if (id == "custom_unit") return@let
            serviceRepository.getServiceById(id)?.let { service ->
                cartRepository.setSelectedServices(setOf(service.id))
                _uiState.update { it.copy(serviceIntent = service.title.uppercase()) }
            } ?: motorcycleRepository.getMotorcycleById(id)?.let { mc ->
                _uiState.update { it.copy(
                    manufacturer = mc.brand,
                    model = mc.model,
                    year = mc.year.toString()
                ) }
            }
        }
    }

    private fun updateIntentFromSelection(services: List<com.example.mad_final.domain.models.WorkshopService>, ids: Set<String>) {
        val firstSelected = services.find { ids.contains(it.id) }
        _uiState.update { it.copy(serviceIntent = firstSelected?.title ?: "CUSTOM SERVICE") }
    }

    fun updateManufacturer(value: String) = _uiState.update { it.copy(manufacturer = value) }
    fun updateModel(value: String) = _uiState.update { it.copy(model = value) }
    fun updateYear(value: String) = _uiState.update { it.copy(year = value) }
    fun updateSelectedDate(value: Long?) = _uiState.update { it.copy(selectedDate = value) }
    fun updateSelectedTime(value: String) = _uiState.update { it.copy(selectedTime = value) }

    fun toggleService(serviceId: String) {
        cartRepository.toggleService(serviceId)
        updateIntentFromSelection(_uiState.value.services, cartRepository.selectedServiceIds.value)
    }

    fun nextStep() { if (_uiState.value.step < 5) _uiState.update { it.copy(step = it.step + 1) } }
    fun previousStep() { if (_uiState.value.step > 1) _uiState.update { it.copy(step = it.step - 1) } }

    fun confirmBooking() {
        viewModelScope.launch {
            val userId = authRepository.getUserId().first() 
                ?: authRepository.getGuestId().first() 
                ?: "guest_${UUID.randomUUID().toString().take(8)}".also { authRepository.saveGuestId(it) }

            val state = _uiState.value
            val selectedTitles = state.services
                .filter { state.selectedServiceIds.contains(it.id) }
                .joinToString(", ") { it.title }
            
            val calendar = java.util.Calendar.getInstance().apply {
                timeInMillis = state.selectedDate ?: System.currentTimeMillis()
                try {
                    val hour = state.selectedTime.substring(0, 2).toInt()
                    val minute = state.selectedTime.substring(3, 5).toInt()
                    val isPM = state.selectedTime.contains("PM")
                    set(java.util.Calendar.HOUR, if (hour == 12) 0 else hour)
                    set(java.util.Calendar.MINUTE, minute)
                    set(java.util.Calendar.AM_PM, if (isPM) java.util.Calendar.PM else java.util.Calendar.AM)
                } catch (e: Exception) {
                    set(java.util.Calendar.HOUR_OF_DAY, 9)
                }
            }

            bookingRepository.createBooking(Booking(
                id = UUID.randomUUID().toString(),
                motorcycleId = "custom_unit",
                userId = userId,
                startDate = calendar.timeInMillis,
                endDate = calendar.timeInMillis + 7200000,
                totalPrice = state.totalPrice,
                status = BookingStatus.CONFIRMED,
                paymentStatus = PaymentStatus.PAID,
                serviceNotes = "Time: ${state.selectedTime}",
                workDescription = selectedTitles,
                customBrand = state.manufacturer,
                customModel = state.model,
                customYear = state.year,
                descriptionDetail = state.serviceIntent
            ))

            notificationHelper.showBookingNotification("Booking Confirmed!", "Service scheduled for ${state.selectedTime}.")
            cartRepository.clearCart()
            nextStep()
        }
    }
}
