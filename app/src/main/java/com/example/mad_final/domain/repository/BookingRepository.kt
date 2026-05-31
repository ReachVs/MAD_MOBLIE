package com.example.mad_final.domain.repository

import com.example.mad_final.domain.models.Booking
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    fun getBookings(): Flow<List<Booking>>
    fun getBookingsByUserId(userId: String): Flow<List<Booking>>
    suspend fun createBooking(booking: Booking)
    suspend fun updateBooking(booking: Booking)
    suspend fun cancelBooking(id: String)
}
