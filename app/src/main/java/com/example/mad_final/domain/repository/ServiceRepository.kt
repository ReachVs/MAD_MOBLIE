package com.example.mad_final.domain.repository

import com.example.mad_final.domain.models.WorkshopService
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    fun getServices(): Flow<List<WorkshopService>>
    fun getServiceByIdFlow(id: String): Flow<WorkshopService?>
    suspend fun getServiceById(id: String): WorkshopService?
    suspend fun updateService(service: WorkshopService)
}
