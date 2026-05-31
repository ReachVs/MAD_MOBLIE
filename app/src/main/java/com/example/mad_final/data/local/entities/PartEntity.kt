package com.example.mad_final.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parts")
data class PartEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val sku: String,
    val stockQuantity: Int,
    val price: Double,
    val category: String,
    val lastRestocked: Long = System.currentTimeMillis()
)
