package com.example.mad_final.data.remote.dto.auth

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
