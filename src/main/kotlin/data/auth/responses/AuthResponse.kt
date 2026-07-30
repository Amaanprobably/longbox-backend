package com.example.data.auth.responses

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val authToken: String,
    val refreshToken: String,
)
