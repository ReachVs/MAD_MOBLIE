package com.example.mad_final.domain.repository

import com.example.mad_final.data.remote.dto.auth.AuthResponse
import com.example.mad_final.data.remote.dto.auth.LoginRequest
import com.example.mad_final.data.remote.dto.auth.RegisterRequest

interface AuthRepository {
    suspend fun login(request: LoginRequest): Result<AuthResponse>
    suspend fun register(request: RegisterRequest): Result<AuthResponse>
    suspend fun logout()
    fun isUserLoggedIn(): kotlinx.coroutines.flow.Flow<Boolean>
    fun getUserRole(): kotlinx.coroutines.flow.Flow<String?>
    fun getUserEmail(): kotlinx.coroutines.flow.Flow<String?>
}
