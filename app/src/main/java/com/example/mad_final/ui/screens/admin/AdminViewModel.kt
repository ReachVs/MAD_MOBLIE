package com.example.mad_final.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mad_final.domain.models.Motorcycle
import com.example.mad_final.domain.repository.AuthRepository
import com.example.mad_final.domain.repository.MotorcycleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: MotorcycleRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val motorcycles: StateFlow<List<Motorcycle>> = repository.getMotorcycles()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        refresh()
    }

    fun addMotorcycle(motorcycle: Motorcycle) {
        viewModelScope.launch {
            repository.addMotorcycle(motorcycle)
        }
    }

    fun updateMotorcycle(motorcycle: Motorcycle) {
        viewModelScope.launch {
            repository.updateMotorcycle(motorcycle)
        }
    }

    fun deleteMotorcycle(id: String) {
        viewModelScope.launch {
            repository.deleteMotorcycle(id)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refreshMotorcycles()
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
