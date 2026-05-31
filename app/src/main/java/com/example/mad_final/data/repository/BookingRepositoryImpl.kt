package com.example.mad_final.data.repository

import com.example.mad_final.data.local.dao.BookingDao
import com.example.mad_final.data.local.entities.toDomain
import com.example.mad_final.data.local.entities.toEntity
import com.example.mad_final.domain.models.Booking
import com.example.mad_final.domain.repository.BookingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val dao: BookingDao
) : BookingRepository {

    override fun getBookings(): Flow<List<Booking>> {
        return dao.getAllBookings().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getBookingsByUserId(userId: String): Flow<List<Booking>> {
        return dao.getBookingsByUserId(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun createBooking(booking: Booking) {
        dao.insertBooking(booking.toEntity())
    }

    override suspend fun updateBooking(booking: Booking) {
        dao.updateBooking(booking.toEntity())
    }

    override suspend fun cancelBooking(id: String) {
        dao.deleteBookingById(id)
    }
}
