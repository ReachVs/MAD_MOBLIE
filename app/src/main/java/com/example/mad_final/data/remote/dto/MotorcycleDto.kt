package com.example.mad_final.data.remote.dto

import com.example.mad_final.data.local.entities.MotorcycleEntity
import com.example.mad_final.domain.models.Motorcycle

data class MotorcycleDto(
    val id: String,
    val brand: String,
    val model: String,
    val year: Int,
    val pricePerDay: Double,
    val availability: Boolean,
    val imageUrl: String,
    val description: String,
    val type: String
)

fun MotorcycleDto.toDomain() = Motorcycle(
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

fun MotorcycleDto.toEntity() = MotorcycleEntity(
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

fun Motorcycle.toDto() = MotorcycleDto(
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
