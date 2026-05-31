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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val repository: MotorcycleRepository,
    private val serviceRepository: com.example.mad_final.domain.repository.ServiceRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("ALL")
    val selectedCategory: StateFlow<String> = _selectedCategory

    val motorcycles: StateFlow<List<Motorcycle>> = repository.getMotorcycles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val services: StateFlow<List<com.example.mad_final.domain.models.WorkshopService>> = 
        combine(serviceRepository.getServices(), _selectedCategory) { services, category ->
            if (category == "ALL") services
            else services.filter { it.category.equals(category, ignoreCase = true) }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        refresh()
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
