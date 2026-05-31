package com.example.mad_final.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mad_final.domain.models.Motorcycle

@Entity(tableName = "motorcycles")
data class MotorcycleEntity(
    @PrimaryKey val id: String,
    val brand: String,
    val model: String,
    val year: Int,
    val pricePerDay: Double,
    val availability: Boolean,
    val imageUrl: String,
    val description: String,
    val type: String
)

fun MotorcycleEntity.toDomain() = Motorcycle(
    id = id,
    brand = brand,
    model = model,
    year = year,
    pricePerDay = pricePerDay,
    availability = availability,
    imageUrl = imageUrl,
    description = description,
    type = type
)

fun Motorcycle.toEntity() = MotorcycleEntity(
    id = id,
    brand = brand,
    model = model,
    year = year,
    pricePerDay = pricePerDay,
    availability = availability,
    imageUrl = imageUrl,
    description = description,
    type = type
)
