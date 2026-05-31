package com.example.mad_final.domain.models

data class Part(
    val id: Int,
    val name: String,
    val sku: String,
    val stockQuantity: Int,
    val price: Double,
    val category: String,
    val lastRestocked: Long
)
