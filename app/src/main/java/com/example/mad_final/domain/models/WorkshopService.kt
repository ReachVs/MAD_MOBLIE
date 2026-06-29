package com.example.mad_final.domain.models

data class WorkshopService(
    val id: String,
    val title: String,
    val price: Double,
    val duration: String,
    val description: String,
    val imageUrl: String,
    val tags: List<String>,
    val category: String,
    val subCategory: String? = null
)
