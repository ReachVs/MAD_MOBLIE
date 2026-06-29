package com.example.mad_final.domain.models

data class Booking(
    val id: String,
    val motorcycleId: String,
    val userId: String,
    val startDate: Long,
    val endDate: Long,
    val totalPrice: Double,
    val status: BookingStatus,
    val paymentStatus: PaymentStatus,
    val serviceNotes: String = "",
    val workDescription: String = "",
    val technicianName: String = "Unassigned",
    val priority: Priority = Priority.NORMAL,
    val usedPartIds: List<Int> = emptyList(),
    // Manual specs for custom units
    val customBrand: String? = null,
    val customModel: String? = null,
    val customYear: String? = null,
    val descriptionDetail: String? = null
)

enum class BookingStatus {
    PENDING, 
    REPAIR, 
    WAITING_PART, 
    READY_TO_PICK_UP,
    CONFIRMED, 
    COMPLETED, 
    CANCELLED
}

enum class Priority {
    LOW, NORMAL, HIGH, URGENT
}

enum class PaymentStatus {
    UNPAID, PAID, REFUNDED
}
