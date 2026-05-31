package com.example.mad_final.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mad_final.data.local.dao.BookingDao
import com.example.mad_final.data.local.dao.MotorcycleDao
import com.example.mad_final.data.local.dao.PartDao
import com.example.mad_final.data.local.dao.ServiceDao
import com.example.mad_final.data.local.entities.BookingEntity
import com.example.mad_final.data.local.entities.MotorcycleEntity
import com.example.mad_final.data.local.entities.PartEntity
import com.example.mad_final.data.local.entities.ServiceEntity

@Database(
    entities = [MotorcycleEntity::class, BookingEntity::class, ServiceEntity::class, PartEntity::class],
    version = 18,
    exportSchema = false
)
abstract class ApexDatabase : RoomDatabase() {
    abstract val motorcycleDao: MotorcycleDao
    abstract val bookingDao: BookingDao
    abstract val serviceDao: ServiceDao
    abstract val partDao: PartDao

    companion object {
        const val DATABASE_NAME = "apex_motorworks_db"
    }
}
