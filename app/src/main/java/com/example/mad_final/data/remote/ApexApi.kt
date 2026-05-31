package com.example.mad_final.data.remote

import com.example.mad_final.data.remote.dto.MotorcycleDto
import com.example.mad_final.data.remote.dto.auth.AuthResponse
import com.example.mad_final.data.remote.dto.auth.LoginRequest
import com.example.mad_final.data.remote.dto.auth.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApexApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("motorcycles")
    suspend fun getMotorcycles(): List<MotorcycleDto>

    @GET("motorcycles/{id}")
    suspend fun getMotorcycleById(@Path("id") id: String): MotorcycleDto

    @POST("motorcycles")
    suspend fun addMotorcycle(@Body motorcycle: MotorcycleDto)

    @PUT("motorcycles/{id}")
    suspend fun updateMotorcycle(@Path("id") id: String, @Body motorcycle: MotorcycleDto)

    @DELETE("motorcycles/{id}")
    suspend fun deleteMotorcycle(@Path("id") id: String)

    // Add other endpoints as needed (Auth, Bookings, etc.)
    
    companion object {
        const val BASE_URL = "https://api.apexmotorworks.com/v1/" // Placeholder
    }
}
