package com.example.mad_final.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mad_final.domain.models.Booking
import com.example.mad_final.domain.models.BookingStatus
import com.example.mad_final.domain.models.PaymentStatus
import com.example.mad_final.domain.models.Priority

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val motorcycleId: String,
    val userId: String,
    val startDate: Long,
    val endDate: Long,
    val totalPrice: Double,
    val status: String,
    val paymentStatus: String,
    val serviceNotes: String = "",
    val workDescription: String = "",
    val technicianName: String = "Unassigned",
    val priority: String = "NORMAL"
)

fun BookingEntity.toDomain() = Booking(
    id = id,
    motorcycleId = motorcycleId,
    userId = userId,
    startDate = startDate,
    endDate = endDate,
    totalPrice = totalPrice,
    status = BookingStatus.valueOf(status),
    paymentStatus = PaymentStatus.valueOf(paymentStatus),
    serviceNotes = serviceNotes,
    workDescription = workDescription,
    technicianName = technicianName,
    priority = Priority.valueOf(priority)
)

fun Booking.toEntity() = BookingEntity(
    id = id,
    motorcycleId = motorcycleId,
    userId = userId,
    startDate = startDate,
    endDate = endDate,
    totalPrice = totalPrice,
    status = status.name,
    paymentStatus = paymentStatus.name,
    serviceNotes = serviceNotes,
    workDescription = workDescription,
    technicianName = technicianName,
    priority = priority.name
)
