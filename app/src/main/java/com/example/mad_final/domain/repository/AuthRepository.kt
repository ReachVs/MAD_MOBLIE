package com.example.mad_final.domain.repository

import com.example.mad_final.data.remote.dto.auth.AuthResponse
import com.example.mad_final.data.remote.dto.auth.LoginRequest
import com.example.mad_final.data.remote.dto.auth.RegisterRequest

interface AuthRepository {
    suspend fun login(request: LoginRequest): Result<AuthResponse>
    suspend fun register(request: RegisterRequest): Result<AuthResponse>
    suspend fun logout()
    fun isUserLoggedIn(): kotlinx.coroutines.flow.Flow<Boolean>
    fun getUserId(): kotlinx.coroutines.flow.Flow<String?>
    fun getUserRole(): kotlinx.coroutines.flow.Flow<String?>
    fun getUserEmail(): kotlinx.coroutines.flow.Flow<String?>
    fun getUserName(): kotlinx.coroutines.flow.Flow<String?>
    fun getUserImageUri(): kotlinx.coroutines.flow.Flow<String?>
    fun getAdminImageUri(): kotlinx.coroutines.flow.Flow<String?>
    fun getGuestId(): kotlinx.coroutines.flow.Flow<String?>
    suspend fun saveGuestId(id: String)
    suspend fun updateProfile(name: String, email: String, imageUri: String?, isAdmin: Boolean = false)
    suspend fun updatePassword(password: String): Result<Unit>
}
