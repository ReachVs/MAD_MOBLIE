package com.example.mad_final.data.repository

import com.example.mad_final.domain.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor() : CartRepository {
    private val _selectedServiceIds = MutableStateFlow<Set<String>>(emptySet())
    override val selectedServiceIds: StateFlow<Set<String>> = _selectedServiceIds.asStateFlow()

    override fun toggleService(serviceId: String) {
        val current = _selectedServiceIds.value
        _selectedServiceIds.value = if (current.contains(serviceId)) {
            current - serviceId
        } else {
            current + serviceId
        }
    }

    override fun setSelectedServices(serviceIds: Set<String>) {
        _selectedServiceIds.value = serviceIds
    }

    override fun clearCart() {
        _selectedServiceIds.value = emptySet()
    }
}
