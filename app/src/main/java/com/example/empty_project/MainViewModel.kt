package com.example.empty_project

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class MainViewModel(
    private val state: SavedStateHandle,
    private val generator: GreetingGenerator = GreetingGenerator(),
    private val timeProvider: TimeProvider = DefaultTimeProvider()
) : ViewModel() {

    companion object {
        private const val KEY_IS_CHANGED = "is_changed"
        private const val KEY_NAME = "name"

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                MainViewModel(savedStateHandle)
            }
        }
    }

    val isChanged: LiveData<Boolean> = state.getLiveData(KEY_IS_CHANGED, false)
    val name: LiveData<String> = state.getLiveData(KEY_NAME, "")

    private val _greetingResource = MutableLiveData<Int>()
    val greetingResource: LiveData<Int> = _greetingResource

    private val _nameError = MutableLiveData<Boolean>(false)
    val nameError: LiveData<Boolean> = _nameError

    fun setName(newName: String) {
        state[KEY_NAME] = newName
        val isValid = NameValidator.isValid(newName) || newName.isEmpty()
        _nameError.value = !isValid
        updateGreeting()
    }

    fun updateGreeting() {
        val hour = timeProvider.getCurrentHour()
        val timeOfDay = generator.getTimeOfDay(hour)
        _greetingResource.value = generator.getGreeting(isChanged.value ?: false, timeOfDay)
    }

    fun toggleGreeting() {
        state[KEY_IS_CHANGED] = !(isChanged.value ?: false)
        updateGreeting()
    }

    fun reset() {
        state[KEY_NAME] = ""
        state[KEY_IS_CHANGED] = false
        _nameError.value = false
        updateGreeting()
    }
}
