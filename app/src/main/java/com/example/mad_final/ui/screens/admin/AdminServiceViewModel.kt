package com.example.mad_final.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mad_final.domain.models.WorkshopService
import com.example.mad_final.domain.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AdminServiceViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    val services: StateFlow<List<WorkshopService>> = serviceRepository.getServices()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow<ServiceUiState>(ServiceUiState.Idle)
    val uiState: StateFlow<ServiceUiState> = _uiState.asStateFlow()

    fun addService(
        title: String,
        price: Double,
        duration: String,
        description: String,
        imageUrl: String,
        category: String,
        tags: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            try {
                val service = WorkshopService(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    price = price,
                    duration = duration,
                    description = description,
                    imageUrl = imageUrl,
                    category = category,
                    tags = tags
                )
                serviceRepository.insertService(service)
                _uiState.value = ServiceUiState.Success("Service added successfully")
            } catch (e: Exception) {
                _uiState.value = ServiceUiState.Error(e.message ?: "Failed to add service")
            }
        }
    }

    fun updateService(service: WorkshopService) {
        viewModelScope.launch {
            try {
                serviceRepository.updateService(service)
                _uiState.value = ServiceUiState.Success("Service updated successfully")
            } catch (e: Exception) {
                _uiState.value = ServiceUiState.Error(e.message ?: "Failed to update service")
            }
        }
    }

    fun deleteService(service: WorkshopService) {
        viewModelScope.launch {
            try {
                serviceRepository.deleteService(service)
                _uiState.value = ServiceUiState.Success("Service deleted successfully")
            } catch (e: Exception) {
                _uiState.value = ServiceUiState.Error(e.message ?: "Failed to delete service")
            }
        }
    }

    fun resetState() {
        _uiState.value = ServiceUiState.Idle
    }
}

sealed class ServiceUiState {
    object Idle : ServiceUiState()
    data class Success(val message: String) : ServiceUiState()
    data class Error(val message: String) : ServiceUiState()
}
