package com.example.mad_final.domain.models

data class Motorcycle(
    val id: String,
    val brand: String,
    val model: String,
    val year: Int,
    val pricePerDay: Double,
    val availability: Boolean,
    val imageUrl: String,
    val description: String,
    val type: String // e.g., Sport, Cruiser, Off-road
)
