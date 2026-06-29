package com.example.mad_final.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface CartRepository {
    val selectedServiceIds: StateFlow<Set<String>>
    fun toggleService(serviceId: String)
    fun setSelectedServices(serviceIds: Set<String>)
    fun clearCart()
}
