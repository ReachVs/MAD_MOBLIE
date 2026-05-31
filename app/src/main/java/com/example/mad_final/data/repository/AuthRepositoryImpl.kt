package com.example.mad_final.data.repository

import com.example.mad_final.data.local.datastore.UserPreferences
import com.example.mad_final.data.remote.ApexApi
import com.example.mad_final.data.remote.dto.auth.AuthResponse
import com.example.mad_final.data.remote.dto.auth.LoginRequest
import com.example.mad_final.data.remote.dto.auth.RegisterRequest
import com.example.mad_final.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: ApexApi,
    private val userPreferences: UserPreferences
) : AuthRepository {

    override suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val response = api.login(request)
            // Use the email from the response or check specifically for admin
            val role = if (response.email.contains("admin", ignoreCase = true)) "ADMIN" else "CUSTOMER"
            userPreferences.saveUserData(response.token, role, response.email)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return try {
            val response = api.register(request)
            userPreferences.saveUserData(response.token, "CUSTOMER", response.email)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        userPreferences.clearToken()
    }

    override fun isUserLoggedIn(): Flow<Boolean> {
        return userPreferences.authToken.map { it != null }
    }

    override fun getUserRole(): Flow<String?> {
        return userPreferences.userRole
    }

    override fun getUserEmail(): Flow<String?> {
        return userPreferences.userEmail
    }
}
