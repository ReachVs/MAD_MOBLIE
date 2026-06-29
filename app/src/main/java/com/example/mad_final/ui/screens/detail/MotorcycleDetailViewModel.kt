package com.example.mad_final.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mad_final.domain.models.Motorcycle
import com.example.mad_final.domain.repository.AuthRepository
import com.example.mad_final.domain.repository.MotorcycleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MotorcycleDetailViewModel @Inject constructor(
    private val repository: MotorcycleRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val motorcycleId: String = checkNotNull(savedStateHandle["motorcycleId"])

    private val _motorcycle = MutableStateFlow<Motorcycle?>(null)
    val motorcycle: StateFlow<Motorcycle?> = _motorcycle

    private val _userImageUri = MutableStateFlow<String?>(null)
    val userImageUri: StateFlow<String?> = _userImageUri.asStateFlow()

    init {
        loadMotorcycle()
        loadUserImage()
    }

    private fun loadUserImage() {
        viewModelScope.launch {
            authRepository.getUserImageUri().collect {
                _userImageUri.value = it
            }
        }
    }

    private fun loadMotorcycle() {
        viewModelScope.launch {
            _motorcycle.value = repository.getMotorcycleById(motorcycleId)
        }
    }
}
