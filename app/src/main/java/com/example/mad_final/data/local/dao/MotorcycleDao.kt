package com.example.mad_final.data.local.dao

import androidx.room.*
import com.example.mad_final.data.local.entities.MotorcycleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MotorcycleDao {
    @Query("SELECT * FROM motorcycles")
    fun getAllMotorcycles(): Flow<List<MotorcycleEntity>>

    @Query("SELECT * FROM motorcycles WHERE id = :id")
    suspend fun getMotorcycleById(id: String): MotorcycleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMotorcycles(motorcycles: List<MotorcycleEntity>)

    @Update
    suspend fun updateMotorcycle(motorcycle: MotorcycleEntity)

    @Delete
    suspend fun deleteMotorcycle(motorcycle: MotorcycleEntity)
}
