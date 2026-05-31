package com.example.mad_final.domain.repository

import com.example.mad_final.domain.models.Part
import kotlinx.coroutines.flow.Flow

interface PartRepository {
    fun getAllParts(): Flow<List<Part>>
    suspend fun getPartById(id: Int): Part?
    suspend fun addPart(part: Part)
    suspend fun updatePart(part: Part)
    suspend fun deletePart(part: Part)
    suspend fun updateStock(id: Int, newQuantity: Int)
}
