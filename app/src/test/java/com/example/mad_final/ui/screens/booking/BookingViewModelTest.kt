package com.example.mad_final.ui.screens.booking

import androidx.lifecycle.SavedStateHandle
import com.example.mad_final.domain.models.*
import com.example.mad_final.domain.repository.*
import com.example.mad_final.ui.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class BookingViewModelTest {

    private lateinit var viewModel: BookingViewModel
    private val motorcycleRepository: MotorcycleRepository = mock()
    private val bookingRepository: BookingRepository = mock()
    private val serviceRepository: ServiceRepository = mock()
    private val authRepository: AuthRepository = mock()
    private val cartRepository: CartRepository = mock()
    private val notificationHelper: NotificationHelper = mock()
    
    private val testDispatcher = StandardTestDispatcher()

    private val mockServices = listOf(
        WorkshopService(
            id = "1", 
            title = "Oil Change", 
            price = 20.00, 
            duration = "30m", 
            description = "Change oil", 
            imageUrl = "", 
            tags = emptyList(), 
            category = "Maintenance",
            subCategory = "Fluid Service"
        ),
        WorkshopService(
            id = "2", 
            title = "Brake Check", 
            price = 15.00,
            duration = "20m", 
            description = "Check brakes", 
            imageUrl = "", 
            tags = emptyList(), 
            category = "Maintenance",
            subCategory = "Safety Inspection"
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        whenever(serviceRepository.getServices()).thenReturn(flowOf(mockServices))
        whenever(cartRepository.selectedServiceIds).thenReturn(MutableStateFlow(emptySet()))
        whenever(authRepository.getUserImageUri()).thenReturn(flowOf(null))
        whenever(authRepository.getUserId()).thenReturn(flowOf("user123"))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun initViewModel(config: String? = null, motorcycleId: String? = null) {
        val handle = SavedStateHandle().apply {
            if (config != null) set("config", config)
            if (motorcycleId != null) set("motorcycleId", motorcycleId)
        }
        viewModel = BookingViewModel(
            motorcycleRepository,
            bookingRepository,
            serviceRepository,
            authRepository,
            cartRepository,
            notificationHelper,
            handle
        )
        testDispatcher.scheduler.runCurrent()
    }

    @Test
    fun `initial state is correct when starting fresh`() = runTest {
        initViewModel()
        val state = viewModel.uiState.value
        assertEquals(1, state.step)
        assertFalse(state.isFromCatalog)
        assertEquals("", state.manufacturer)
        assertEquals(mockServices, state.services)
    }

    @Test
    fun `initial state is correct when coming from catalog`() = runTest {
        initViewModel(config = "1,2")
        val state = viewModel.uiState.value
        assertTrue(state.isFromCatalog)
        verify(cartRepository).setSelectedServices(setOf("1", "2"))
    }

    @Test
    fun `step navigation works correctly`() = runTest {
        initViewModel()
        
        viewModel.nextStep()
        assertEquals(2, viewModel.uiState.value.step)
        
        viewModel.previousStep()
        assertEquals(1, viewModel.uiState.value.step)
        
        // Check boundaries
        viewModel.previousStep()
        assertEquals(1, viewModel.uiState.value.step)
        
        repeat(10) { viewModel.nextStep() }
        assertEquals(5, viewModel.uiState.value.step)
    }

    @Test
    fun `updating manufacturer updates state and validity`() = runTest {
        initViewModel()
        assertFalse(viewModel.uiState.value.isMotorcycleValid)
        
        viewModel.updateManufacturer("Ducati")
        viewModel.updateModel("Panigale")
        viewModel.updateYear("2023")
        testDispatcher.scheduler.runCurrent()
        
        val state = viewModel.uiState.value
        assertEquals("Ducati", state.manufacturer)
        assertTrue(state.isMotorcycleValid)
    }

    @Test
    fun `toggleService updates cart and price`() = runTest {
        val selectedIdsFlow = MutableStateFlow<Set<String>>(emptySet())
        whenever(cartRepository.selectedServiceIds).thenReturn(selectedIdsFlow)
        
        initViewModel()
        
        // Mock the behavior of cartRepository.toggleService
        doAnswer {
            val id = it.arguments[0] as String
            selectedIdsFlow.value = if (selectedIdsFlow.value.contains(id)) {
                selectedIdsFlow.value - id
            } else {
                selectedIdsFlow.value + id
            }
            null
        }.whenever(cartRepository).toggleService(any())

        viewModel.toggleService("1")
        testDispatcher.scheduler.runCurrent()
        
        val state = viewModel.uiState.value
        assertEquals(setOf("1"), state.selectedServiceIds)
        assertEquals(20.0, state.totalPrice, 0.01)
        assertTrue(state.isServiceValid)
        
        viewModel.toggleService("2")
        testDispatcher.scheduler.runCurrent()
        assertEquals(35.0, viewModel.uiState.value.totalPrice, 0.01)
    }

    @Test
    fun `initial data pre-fills from motorcycleId`() = runTest {
        val mockMc = Motorcycle(
            id = "moto1",
            brand = "Ducati",
            model = "Panigale",
            year = 2023,
            pricePerDay = 299.0,
            availability = true,
            imageUrl = "",
            description = "High performance machine",
            type = "Sport"
        )
        whenever(motorcycleRepository.getMotorcycleById("moto1")).thenReturn(mockMc)
        
        initViewModel(motorcycleId = "moto1")
        
        val state = viewModel.uiState.value
        assertEquals("Ducati", state.manufacturer)
        assertEquals("Panigale", state.model)
        assertEquals("2023", state.year)
        assertTrue(state.isMotorcycleValid)
    }

    @Test
    fun `isServiceValid reflects selection state`() = runTest {
        val selectedIdsFlow = MutableStateFlow<Set<String>>(emptySet())
        whenever(cartRepository.selectedServiceIds).thenReturn(selectedIdsFlow)
        
        initViewModel()
        assertFalse(viewModel.uiState.value.isServiceValid)
        
        selectedIdsFlow.value = setOf("1")
        testDispatcher.scheduler.runCurrent()
        assertTrue(viewModel.uiState.value.isServiceValid)
    }

    @Test
    fun `confirmBooking calculates correct arrival time`() = runTest {
        initViewModel()
        viewModel.updateSelectedDate(1704067200000L) // Jan 1, 2024
        viewModel.updateSelectedTime("10:30 AM")
        
        viewModel.confirmBooking()
        testDispatcher.scheduler.runCurrent()
        
        argumentCaptor<Booking>().apply {
            verify(bookingRepository).createBooking(capture())
            val booking = firstValue
            // 10:30 AM on Jan 1, 2024
            val cal = java.util.Calendar.getInstance().apply { timeInMillis = booking.startDate }
            assertEquals(10, cal.get(java.util.Calendar.HOUR))
            assertEquals(30, cal.get(java.util.Calendar.MINUTE))
            assertEquals(java.util.Calendar.AM, cal.get(java.util.Calendar.AM_PM))
        }
    }
}
