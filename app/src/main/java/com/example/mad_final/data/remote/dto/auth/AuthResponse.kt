package com.example.mad_final.data.remote.dto.auth

data class AuthResponse(
    val token: String,
    val userId: String,
    val email: String,
    val name: String
)
