package com.example.mad_final.domain.repository

import com.example.mad_final.domain.models.Motorcycle
import kotlinx.coroutines.flow.Flow

interface MotorcycleRepository {
    fun getMotorcycles(): Flow<List<Motorcycle>>
    suspend fun getMotorcycleById(id: String): Motorcycle?
    suspend fun refreshMotorcycles()
    suspend fun addMotorcycle(motorcycle: Motorcycle): Result<Unit>
    suspend fun updateMotorcycle(motorcycle: Motorcycle): Result<Unit>
    suspend fun deleteMotorcycle(id: String): Result<Unit>
}
