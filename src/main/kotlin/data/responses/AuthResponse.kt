package com.example.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val authToken: String,
    val refreshToken: String,
)
