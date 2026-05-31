package com.example.mad_final.data.local.dao

import androidx.room.*
import com.example.mad_final.data.local.entities.ServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services")
    fun getAllServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE id = :id")
    suspend fun getServiceById(id: String): ServiceEntity?

    @Query("SELECT * FROM services WHERE id = :id")
    fun getServiceByIdFlow(id: String): Flow<ServiceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity)

    @Update
    suspend fun updateService(service: ServiceEntity)

    @Delete
    suspend fun deleteService(service: ServiceEntity)
}
