package com.example.mad_final.data.repository

import com.example.mad_final.data.local.dao.PartDao
import com.example.mad_final.data.local.entities.PartEntity
import com.example.mad_final.domain.models.Part
import com.example.mad_final.domain.repository.PartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PartRepositoryImpl @Inject constructor(
    private val partDao: PartDao
) : PartRepository {

    override fun getAllParts(): Flow<List<Part>> {
        return partDao.getAllParts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPartById(id: Int): Part? {
        return partDao.getPartById(id)?.toDomain()
    }

    override suspend fun addPart(part: Part) {
        partDao.insertPart(part.toEntity())
    }

    override suspend fun updatePart(part: Part) {
        partDao.updatePart(part.toEntity())
    }

    override suspend fun deletePart(part: Part) {
        partDao.deletePart(part.toEntity())
    }

    override suspend fun updateStock(id: Int, newQuantity: Int) {
        partDao.updateStock(id, newQuantity)
    }

    private fun PartEntity.toDomain() = Part(
        id = id,
        name = name,
        sku = sku,
        stockQuantity = stockQuantity,
        price = price,
        category = category,
        lastRestocked = lastRestocked
    )

    private fun Part.toEntity() = PartEntity(
        id = id,
        name = name,
        sku = sku,
        stockQuantity = stockQuantity,
        price = price,
        category = category,
        lastRestocked = lastRestocked
    )
}
