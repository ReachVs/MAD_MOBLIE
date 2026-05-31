package com.example.mad_final.data.repository

import com.example.mad_final.data.local.dao.MotorcycleDao
import com.example.mad_final.data.local.entities.toDomain
import com.example.mad_final.data.local.entities.toEntity
import com.example.mad_final.data.remote.ApexApi
import com.example.mad_final.data.remote.dto.toDto
import com.example.mad_final.data.remote.dto.toEntity
import com.example.mad_final.domain.models.Motorcycle
import com.example.mad_final.domain.repository.MotorcycleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MotorcycleRepositoryImpl @Inject constructor(
    private val api: ApexApi,
    private val dao: MotorcycleDao
) : MotorcycleRepository {

    override fun getMotorcycles(): Flow<List<Motorcycle>> {
        return dao.getAllMotorcycles().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getMotorcycleById(id: String): Motorcycle? {
        return dao.getMotorcycleById(id)?.toDomain()
    }

    override suspend fun refreshMotorcycles() {
        try {
            val motorcyclesDto = api.getMotorcycles()
            dao.insertMotorcycles(motorcyclesDto.map { it.toEntity() })
        } catch (e: Exception) {
            // Handle error (logging, etc.)
        }
    }

    override suspend fun addMotorcycle(motorcycle: Motorcycle): Result<Unit> {
        return try {
            val dto = motorcycle.toDto()
            api.addMotorcycle(dto)
            dao.insertMotorcycles(listOf(dto.toEntity()))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMotorcycle(motorcycle: Motorcycle): Result<Unit> {
        return try {
            val dto = motorcycle.toDto()
            api.updateMotorcycle(motorcycle.id, dto)
            dao.updateMotorcycle(dto.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMotorcycle(id: String): Result<Unit> {
        return try {
            api.deleteMotorcycle(id)
            val entity = dao.getMotorcycleById(id)
            if (entity != null) {
                dao.deleteMotorcycle(entity)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
