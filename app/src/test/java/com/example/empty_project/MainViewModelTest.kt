package com.example.empty_project

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class MainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var generator: GreetingGenerator
    private lateinit var timeProvider: TimeProvider
    private lateinit var state: SavedStateHandle
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        generator = GreetingGenerator() // Use real generator for logic verification
        timeProvider = mock()
        state = SavedStateHandle()
        viewModel = MainViewModel(state, generator, timeProvider)
    }

    @Test
    fun updateGreeting_morning_setsCorrectResource() {
        whenever(timeProvider.getCurrentHour()).thenReturn(8)
        
        viewModel.updateGreeting()
        
        assertEquals(R.string.good_morning, viewModel.greetingResource.value)
    }

    @Test
    fun updateGreeting_night_setsCorrectResource() {
        whenever(timeProvider.getCurrentHour()).thenReturn(23)
        
        viewModel.updateGreeting()
        
        assertEquals(R.string.good_night, viewModel.greetingResource.value)
    }

    @Test
    fun toggleGreeting_updatesStateAndResource() {
        whenever(timeProvider.getCurrentHour()).thenReturn(10)
        
        viewModel.toggleGreeting()
        
        assertEquals(true, viewModel.isChanged.value)
        assertEquals(R.string.greeting_changed, viewModel.greetingResource.value)
        
        viewModel.toggleGreeting()
        assertEquals(false, viewModel.isChanged.value)
        assertEquals(R.string.good_morning, viewModel.greetingResource.value)
    }

    @Test
    fun setName_updatesNameAndValidates() {
        whenever(timeProvider.getCurrentHour()).thenReturn(12)
        
        viewModel.setName("Alice")
        
        assertEquals("Alice", viewModel.name.value)
        assertEquals(R.string.good_afternoon, viewModel.greetingResource.value)
        assertEquals(false, viewModel.nameError.value)
    }

    @Test
    fun setName_tooLong_setsError() {
        val longName = "A".repeat(NameValidator.MAX_NAME_LENGTH + 1)
        viewModel.setName(longName)
        
        assertEquals(true, viewModel.nameError.value)
    }

    @Test
    fun reset_restoresInitialState() {
        whenever(timeProvider.getCurrentHour()).thenReturn(20) // Evening
        
        viewModel.setName("Bob")
        viewModel.toggleGreeting()
        
        viewModel.reset()
        
        assertEquals("", viewModel.name.value)
        assertEquals(false, viewModel.isChanged.value)
        assertEquals(false, viewModel.nameError.value)
        assertEquals(R.string.good_evening, viewModel.greetingResource.value)
    }
}
