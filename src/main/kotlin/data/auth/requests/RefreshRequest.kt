package com.example.data.auth.requests

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequest(
    val refreshToken: String,
)
