package com.example.mad_final.ui.screens.admin

import com.example.mad_final.domain.models.*
import com.example.mad_final.domain.repository.BookingRepository
import com.example.mad_final.domain.repository.PartRepository
import com.example.mad_final.ui.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class AdminQueueViewModelTest {

    private lateinit var viewModel: AdminQueueViewModel
    private val bookingRepository: BookingRepository = mock()
    private val partRepository: PartRepository = mock()
    private val authRepository: com.example.mad_final.domain.repository.AuthRepository = mock()
    private val motorcycleRepository: com.example.mad_final.domain.repository.MotorcycleRepository = mock()
    private val notificationHelper: NotificationHelper = mock()
    
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        whenever(bookingRepository.getBookings()).thenReturn(flowOf(emptyList()))
        whenever(partRepository.getAllParts()).thenReturn(flowOf(emptyList()))
        whenever(authRepository.getUserName()).thenReturn(flowOf(null))
        whenever(authRepository.getAdminImageUri()).thenReturn(flowOf(null))
        whenever(motorcycleRepository.getMotorcycles()).thenReturn(flowOf(emptyList()))
        
        viewModel = AdminQueueViewModel(
            bookingRepository, 
            partRepository, 
            authRepository, 
            motorcycleRepository, 
            notificationHelper
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateBookingStatus to COMPLETED decrements stock of used parts`() = runTest {
        // Arrange
        val partId = 101
        val initialStock = 10
        val usedParts = listOf(partId)
        val booking = Booking(
            id = "test_id",
            motorcycleId = "moto_id",
            userId = "user_id",
            startDate = 0L,
            endDate = 0L,
            totalPrice = 100.0,
            status = BookingStatus.REPAIR, // Current status
            paymentStatus = PaymentStatus.UNPAID,
            usedPartIds = usedParts
        )
        
        val part = Part(
            id = partId,
            name = "Test Part",
            sku = "SKU-123",
            stockQuantity = initialStock,
            price = 50.0,
            category = "Engine",
            lastRestocked = 0L
        )

        // Mock getting the current bookings to find the old booking
        whenever(bookingRepository.getBookings()).thenReturn(flowOf(listOf(booking)))
        // Re-init viewModel to pick up the mocked flow
        viewModel = AdminQueueViewModel(
            bookingRepository, 
            partRepository, 
            authRepository, 
            motorcycleRepository, 
            notificationHelper
        )
        testDispatcher.scheduler.runCurrent()

        whenever(partRepository.getPartById(partId)).thenReturn(part)

        // Act
        val updatedBooking = booking.copy(status = BookingStatus.COMPLETED)
        viewModel.updateBookingStatus(updatedBooking)
        testDispatcher.scheduler.runCurrent()

        // Assert
        verify(partRepository).updateStock(partId, initialStock - 1)
        verify(bookingRepository).updateBooking(updatedBooking)
    }
}
