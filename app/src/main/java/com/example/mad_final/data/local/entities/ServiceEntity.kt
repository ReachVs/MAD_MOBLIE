package com.example.mad_final.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mad_final.domain.models.WorkshopService

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val id: String,
    val title: String,
    val price: String,
    val description: String,
    val imageUrl: String,
    val tags: String, // Comma-separated
    val category: String
)

fun ServiceEntity.toDomain(): WorkshopService {
    return WorkshopService(
        id = id,
        title = title,
        price = price,
        description = description,
        imageUrl = imageUrl,
        tags = if (tags.isEmpty()) emptyList() else tags.split(","),
        category = category
    )
}

fun WorkshopService.toEntity(): ServiceEntity {
    return ServiceEntity(
        id = id,
        title = title,
        price = price,
        description = description,
        imageUrl = imageUrl,
        tags = tags.joinToString(","),
        category = category
    )
}
