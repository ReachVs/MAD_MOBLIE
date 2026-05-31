package com.example.mad_final.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mad_final.domain.models.Motorcycle
import com.example.mad_final.domain.models.Part
import com.example.mad_final.domain.repository.MotorcycleRepository
import com.example.mad_final.domain.repository.PartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminInventoryViewModel @Inject constructor(
    private val repository: MotorcycleRepository,
    private val partRepository: PartRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow("ALL")
    val selectedCategory: StateFlow<String> = _selectedCategory

    val motorcycles: StateFlow<List<Motorcycle>> = repository.getMotorcycles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val parts: StateFlow<List<Part>> = combine(
        partRepository.getAllParts(),
        _searchQuery,
        _selectedCategory
    ) { allParts, query, category ->
        allParts.filter { part ->
            val matchesQuery = part.name.contains(query, ignoreCase = true) || 
                             part.sku.contains(query, ignoreCase = true)
            val matchesCategory = category == "ALL" || part.category.uppercase() == category.uppercase()
            matchesQuery && matchesCategory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: String) {
        _selectedCategory.value = category
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refreshMotorcycles()
        }
    }

    fun deleteMotorcycle(id: String) {
        viewModelScope.launch {
            repository.deleteMotorcycle(id)
        }
    }

    fun deletePart(part: Part) {
        viewModelScope.launch {
            partRepository.deletePart(part)
        }
    }

    fun updatePartStock(id: Int, newQuantity: Int) {
        viewModelScope.launch {
            partRepository.updateStock(id, newQuantity)
        }
    }

    fun addPart(name: String, sku: String, quantity: Int, price: Double, category: String) {
        viewModelScope.launch {
            partRepository.addPart(
                Part(
                    id = 0,
                    name = name,
                    sku = sku,
                    stockQuantity = quantity,
                    price = price,
                    category = category,
                    lastRestocked = System.currentTimeMillis()
                )
            )
        }
    }
}
