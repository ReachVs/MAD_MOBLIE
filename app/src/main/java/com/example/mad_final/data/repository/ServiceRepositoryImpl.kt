package com.example.mad_final.data.repository

import com.example.mad_final.data.local.dao.ServiceDao
import com.example.mad_final.data.local.entities.toDomain
import com.example.mad_final.data.local.entities.toEntity
import com.example.mad_final.domain.models.WorkshopService
import com.example.mad_final.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ServiceRepositoryImpl @Inject constructor(
    private val dao: ServiceDao
) : ServiceRepository {

    override fun getServices(): Flow<List<WorkshopService>> {
        return dao.getAllServices().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getServiceByIdFlow(id: String): Flow<WorkshopService?> {
        return dao.getServiceByIdFlow(id).map { it?.toDomain() }
    }

    override suspend fun getServiceById(id: String): WorkshopService? {
        return dao.getServiceById(id)?.toDomain()
    }

    override suspend fun insertService(service: WorkshopService) {
        dao.insertService(service.toEntity())
    }

    override suspend fun updateService(service: WorkshopService) {
        dao.updateService(service.toEntity())
    }

    override suspend fun deleteService(service: WorkshopService) {
        dao.deleteService(service.toEntity())
    }
}
