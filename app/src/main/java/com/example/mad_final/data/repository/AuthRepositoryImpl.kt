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
            userPreferences.saveUserData(
                response.token, 
                response.userId, 
                response.role, 
                response.email,
                response.name
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return try {
            val response = api.register(request)
            userPreferences.saveUserData(
                response.token, 
                response.userId, 
                response.role, 
                response.email,
                response.name
            )
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

    override fun getUserId(): Flow<String?> {
        return userPreferences.userId
    }

    override fun getUserRole(): Flow<String?> {
        return userPreferences.userRole
    }

    override fun getUserEmail(): Flow<String?> {
        return userPreferences.userEmail
    }

    override fun getUserName(): Flow<String?> {
        return userPreferences.userName
    }

    override fun getUserImageUri(): Flow<String?> {
        return userPreferences.userImageUri
    }

    override fun getAdminImageUri(): Flow<String?> {
        return userPreferences.adminImageUri
    }

    override fun getGuestId(): Flow<String?> {
        return userPreferences.guestId
    }

    override suspend fun saveGuestId(id: String) {
        userPreferences.saveGuestId(id)
    }

    override suspend fun updateProfile(name: String, email: String, imageUri: String?, isAdmin: Boolean) {
        userPreferences.updateProfile(name, email, imageUri, isAdmin)
    }

    override suspend fun updatePassword(password: String): Result<Unit> {
        return try {
            // Ideally call API here
            // api.updatePassword(password)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
