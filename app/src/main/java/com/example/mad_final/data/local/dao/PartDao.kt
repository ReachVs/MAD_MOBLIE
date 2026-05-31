package com.example.mad_final.data.local.dao

import androidx.room.*
import com.example.mad_final.data.local.entities.PartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PartDao {
    @Query("SELECT * FROM parts")
    fun getAllParts(): Flow<List<PartEntity>>

    @Query("SELECT * FROM parts WHERE id = :id")
    suspend fun getPartById(id: Int): PartEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPart(part: PartEntity)

    @Update
    suspend fun updatePart(part: PartEntity)

    @Delete
    suspend fun deletePart(part: PartEntity)

    @Query("UPDATE parts SET stockQuantity = :newQuantity WHERE id = :id")
    suspend fun updateStock(id: Int, newQuantity: Int)
}
