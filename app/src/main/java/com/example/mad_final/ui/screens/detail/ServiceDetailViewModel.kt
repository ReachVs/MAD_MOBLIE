package com.example.mad_final.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mad_final.domain.models.WorkshopService
import com.example.mad_final.domain.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServiceDetailViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val serviceId: String? = savedStateHandle["serviceId"]

    private val _service = MutableStateFlow<WorkshopService?>(null)
    val service: StateFlow<WorkshopService?> = _service

    private val _selectedCapacity = MutableStateFlow("1000cc")
    val selectedCapacity: StateFlow<String> = _selectedCapacity

    init {
        loadService()
    }

    fun selectCapacity(capacity: String) {
        _selectedCapacity.value = capacity
    }

    private fun loadService() {
        serviceId?.let { id ->
            viewModelScope.launch {
                serviceRepository.getServiceByIdFlow(id).collect {
                    _service.value = it
                }
            }
        }
    }
}
