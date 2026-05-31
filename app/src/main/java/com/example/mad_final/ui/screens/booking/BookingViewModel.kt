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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val motorcycleRepository: MotorcycleRepository,
    private val bookingRepository: BookingRepository,
    private val serviceRepository: com.example.mad_final.domain.repository.ServiceRepository,
    private val authRepository: AuthRepository,
    private val notificationHelper: NotificationHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val motorcycleId: String? = savedStateHandle["motorcycleId"]
    private val config: String? = savedStateHandle["config"]

    private val _motorcycle = MutableStateFlow<Motorcycle?>(null)
    val motorcycle: StateFlow<Motorcycle?> = _motorcycle

    private val _service = MutableStateFlow<com.example.mad_final.domain.models.WorkshopService?>(null)
    val service: StateFlow<com.example.mad_final.domain.models.WorkshopService?> = _service

    private val _bookingStep = MutableStateFlow(1)
    val bookingStep: StateFlow<Int> = _bookingStep
    
    // Vehicle Details State
    private val _manufacturer = MutableStateFlow("")
    val manufacturer: StateFlow<String> = _manufacturer
    
    private val _model = MutableStateFlow("")
    val model: StateFlow<String> = _model
    
    private val _year = MutableStateFlow("")
    val year: StateFlow<String> = _year
    
    private val _serviceIntent = MutableStateFlow("PERFORMANCE TUNING")
    val serviceIntent: StateFlow<String> = _serviceIntent

    private val _engineCapacity = MutableStateFlow("1000cc")
    val engineCapacity: StateFlow<String> = _engineCapacity

    private val _configuration = MutableStateFlow("")
    val configuration: StateFlow<String> = _configuration

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice

    private val _selectedDate = MutableStateFlow<Long?>(System.currentTimeMillis())
    val selectedDate: StateFlow<Long?> = _selectedDate

    private val _services = MutableStateFlow<List<com.example.mad_final.domain.models.WorkshopService>>(emptyList())
    val services: StateFlow<List<com.example.mad_final.domain.models.WorkshopService>> = _services

    init {
        loadData()
        loadAllServices()
    }

    private fun loadAllServices() {
        viewModelScope.launch {
            serviceRepository.getServices().collect {
                _services.value = it
                syncPriceWithIntent(it)
            }
        }
    }

    private fun syncPriceWithIntent(allServices: List<com.example.mad_final.domain.models.WorkshopService>) {
        val intent = _serviceIntent.value
        if (intent.isNotEmpty()) {
            val matching = allServices.find { it.title.equals(intent, ignoreCase = true) }
            matching?.let {
                _service.value = it
                _totalPrice.value = it.price.replace("$", "").toDoubleOrNull() ?: 0.0
            }
        }
    }

    private fun loadData() {
        config?.let { _configuration.value = it }
        motorcycleId?.let { id ->
            viewModelScope.launch {
                // Try to load as service first
                val serviceData = serviceRepository.getServiceById(id)
                if (serviceData != null) {
                    _service.value = serviceData
                    // Use the exact title from the catalog as the intent
                    _serviceIntent.value = serviceData.title.uppercase()
                    // Extract price: "$150" -> 150.0
                    _totalPrice.value = serviceData.price.replace("$", "").toDoubleOrNull() ?: 0.0
                } else {
                    // Fallback to motorcycle
                    _motorcycle.value = motorcycleRepository.getMotorcycleById(id)
                    _motorcycle.value?.let {
                        _manufacturer.value = it.brand
                        _model.value = it.model
                        _year.value = it.year.toString()
                    }
                }
            }
        }
    }

    fun updateManufacturer(value: String) { _manufacturer.value = value }
    fun updateModel(value: String) { _model.value = value }
    fun updateYear(value: String) { _year.value = value }
    fun updateServiceIntent(value: String) {
        _serviceIntent.value = value
        val matching = _services.value.find { it.title.equals(value, ignoreCase = true) }
        if (matching != null) {
            _service.value = matching
            _totalPrice.value = matching.price.replace("$", "").toDoubleOrNull() ?: 0.0
        } else {
            // Manual intent selection fallback logic
            val price = when {
                value.contains("MAINTENANCE", ignoreCase = true) -> 150.0
                value.contains("OVERHAUL", ignoreCase = true) -> 1200.0
                value.contains("ELECTRICAL", ignoreCase = true) -> 250.0
                value.contains("TUNING", ignoreCase = true) || value.contains("PERFORMANCE", ignoreCase = true) -> 450.0
                else -> 0.0
            }
            _totalPrice.value = price
            _service.value = null
        }
    }
    fun updateEngineCapacity(value: String) { _engineCapacity.value = value }
    fun updateConfiguration(value: String) { _configuration.value = value }
    fun updateSelectedDate(value: Long?) { _selectedDate.value = value }

    fun nextStep() {
        if (_bookingStep.value < 5) {
            _bookingStep.value += 1
        }
    }

    fun previousStep() {
        if (_bookingStep.value > 1) {
            _bookingStep.value -= 1
        }
    }

    fun confirmBooking() {
        viewModelScope.launch {
            val userEmail = authRepository.getUserEmail().first() ?: "anonymous"
            val booking = Booking(
                id = UUID.randomUUID().toString(),
                motorcycleId = motorcycleId ?: "custom_unit",
                userId = userEmail,
                startDate = _selectedDate.value ?: System.currentTimeMillis(),
                endDate = (_selectedDate.value ?: System.currentTimeMillis()) + 86400000,
                totalPrice = _totalPrice.value,
                status = BookingStatus.CONFIRMED,
                paymentStatus = PaymentStatus.PAID
            )
            bookingRepository.createBooking(booking)
            notificationHelper.showBookingNotification(
                "Booking Confirmed!",
                "Your service request for ${_manufacturer.value} ${_model.value} (${_configuration.value}) has been logged."
            )
            _bookingStep.value = 5 // Confirmation step
        }
    }
}
